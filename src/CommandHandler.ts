import type {
  Interaction,
  ChatInputCommandInteraction,
  SlashCommandBuilder,
} from "discord.js";
import type { RESTPostAPIChatInputApplicationCommandsJSONBody } from "discord.js";
import type { Client, Snowflake } from "discord.js";

import { Collection, REST, Routes } from "discord.js";
import path from "path";
import fs from "fs";
import { logger } from "./logger.ts";

export interface CommandBuildData {
  // empty for now
}

export type Command = (
  interaction: ChatInputCommandInteraction,
) => Promise<void>;

export type CommandBuilder = Omit<
  SlashCommandBuilder,
  "addSubcommand" | "addSubcommandGroup"
>;

export type CommandBuilderFactory = (data: CommandBuildData) => CommandBuilder;

export type CommandGuard = (
  interaction: ChatInputCommandInteraction,
) => Promise<boolean>;

export type CommandDescription =
  RESTPostAPIChatInputApplicationCommandsJSONBody; // This type name is hilarious

export type CommandHandlerOptions = {
  commandsDir?: string;
};

export class CommandHandler {
  commandsDir: string;
  commands: Collection<string, Command> = new Collection();

  constructor(options: CommandHandlerOptions) {
    this.commandsDir = path.join(__dirname, options.commandsDir ?? "commands");
  }

  private async createCommand(
    data: CommandBuildData,
    builderFactory: CommandBuilderFactory,
    commandBody: Command,
    guard?: CommandGuard,
  ): Promise<{ description: CommandDescription; command: Command }> {
    const description = builderFactory(data).toJSON();
    // Either wrap the command with a guard check (if provided) or just use the command
    const command = guard
      ? async (interaction: ChatInputCommandInteraction) => {
          if (await guard(interaction)) {
            await command(interaction);
          }
        }
      : commandBody;
    return { description, command };
  }

  private async loadCommands(
    buildData: CommandBuildData,
  ): Promise<CommandDescription[]> {
    // TODO: Files inside subdirectories should become subcommands
    const commandFiles = await fs.promises.readdir(this.commandsDir);

    const commandDescriptions: CommandDescription[] = [];

    for (const file of commandFiles) {
      const commandDefinition = await import(path.join(this.commandsDir, file));
      if (!commandDefinition.build || !commandDefinition.default) {
        logger.warn(
          `Command ${file} has no build function and/or default function defined, skipping...`,
        );
        continue;
      }

      const { build, default: commandBody, guard } = commandDefinition;
      const { description, command } = await this.createCommand(
        buildData,
        build,
        commandBody,
        guard,
      );

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
    await command(interaction);
  }

  public async registerCommands(client: Client, guildId: Snowflake) {
    const commands = await this.loadCommands({});
    const rest = new REST({ version: "10" }).setToken(
      process.env.DISCORD_TOKEN!,
    );
    await rest.put(
      Routes.applicationGuildCommands(process.env.APP_ID!, guildId),
      { body: commands },
    );

    // Get the name of all commands
    const commandNames = commands.map((command) => `/${command.name}`);
    logger.info(
      `Registered ${commandNames.length} commands: ${commandNames.join(", ")}`,
    );

    client.on("interactionCreate", this.handleCommand.bind(this));
  }
}
