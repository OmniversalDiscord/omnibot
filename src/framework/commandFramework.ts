import type {
  Interaction,
  ChatInputCommandInteraction,
  SlashCommandBuilder,
} from "discord.js";
import type { RESTPostAPIChatInputApplicationCommandsJSONBody } from "discord.js";
import type { Client, Snowflake } from "discord.js";
import type {
  Command,
  CommandBuilder,
  CommandGuard,
  Context,
  ContextData,
  RegistrationContext,
} from "./types.ts";

import { Collection, REST, Routes } from "discord.js";
import path from "path";
import fs from "fs";
import { logger } from "../logger.ts";
import { errorMessage } from "./helpers.ts";
import config from "config";

type CommandRegistrationFn = (
  ContextData: RegistrationContext,
) => Promise<CommandBuilder>;

type CommandDescription = RESTPostAPIChatInputApplicationCommandsJSONBody; // This type name is hilarious

export type CommandHandlerOptions = {
  commandsDir: string;
  contextData?: ContextData;
  disabled?: string[];
};

export class CommandFramework {
  commandsDir: string;
  commands: Collection<string, Command> = new Collection();
  disabled: string[];
  restClient: REST;
  contextData: ContextData = {};
  appId: Snowflake;

  constructor(options: CommandHandlerOptions) {
    this.commandsDir = options.commandsDir;
    this.contextData = options.contextData ?? {};

    let token = config.get<string>("discord.token");
    let appId = atob(token.split(".")[0]); // App ID is the first part of the token, base64 encoded

    this.restClient = new REST({ version: "10" }).setToken(token);

    this.appId = appId;
    this.disabled = options.disabled ?? [];
  }

  private async createCommand(
    context: RegistrationContext,
    register: CommandRegistrationFn,
    commandBody: Command,
    guard?: CommandGuard,
  ): Promise<{ description: CommandDescription; command: Command }> {
    const description = (await register(context)).toJSON();

    // Either wrap the command with a guard check (if provided) or just use the command
    const command = guard
      ? async (context: Context) => {
          let result = await guard(context);
          if (result.type === "error") {
            await context.interaction.reply(errorMessage(result.message));
            return;
          }

          context.fromGuard = result.data;
          await commandBody(context);
        }
      : commandBody;

    return { description, command };
  }

  private async loadCommands(
    context: RegistrationContext,
  ): Promise<CommandDescription[]> {
    // TODO: Files inside subdirectories should become subcommands
    const commandFiles = await fs.promises.readdir(this.commandsDir);

    const commandDescriptions: CommandDescription[] = [];

    for (const file of commandFiles) {
      const commandDefinition = await import(path.join(this.commandsDir, file));

      if (!commandDefinition.register || !commandDefinition.default) {
        logger.warn(
          `Command ${file} has no register function and/or default function defined, skipping...`,
        );
        continue;
      }

      const { register, default: commandBody, guard } = commandDefinition;

      const { description, command } = await this.createCommand(
        context,
        register,
        commandBody,
        guard,
      );

      if (this.disabled.includes(description.name)) {
        logger.debug(`Skipping disabled command ${description.name}`);
        continue;
      }

      this.commands.set(description.name, command);
      commandDescriptions.push(description);
    }

    return commandDescriptions;
  }

  public async handleCommand(interaction: Interaction) {
    if (!interaction.isChatInputCommand()) return;

    const command = this.commands.get(interaction.commandName);
    if (!command) {
      logger.warn(
        `Attempted to call /${interaction.commandName}, but no such command exists`,
      );
      return;
    }

    logger.debug(`Handling command /${interaction.commandName}`);
    await command({
      interaction: interaction,
      data: this.contextData,
      fromGuard: null,
    });
  }

  public async registerCommands(client: Client, guildId: Snowflake) {
    const commands = await this.loadCommands({
      restClient: this.restClient,
      data: this.contextData,
    });

    await this.restClient.put(
      Routes.applicationGuildCommands(this.appId, guildId),
      {
        body: commands,
      },
    );

    // Get the name of all commands
    const commandNames = commands.map((command) => `/${command.name}`);
    logger.info(
      `Registered ${commandNames.length} commands: ${commandNames.join(", ")}`,
    );

    client.on("interactionCreate", this.handleCommand.bind(this));
  }
}
