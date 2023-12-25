import "dotenv/config";
import { Client, GatewayIntentBits, Snowflake } from "discord.js";
import { logger } from "./logger.ts";
import { CommandHandler } from "./framework/commandHandler.ts";
import config from "config";
import path from "path";
import { roleSectionCache } from "./models/roleSectionCache.ts";

const client = new Client({
  intents: [
    GatewayIntentBits.Guilds,
    GatewayIntentBits.GuildMembers,
    GatewayIntentBits.GuildModeration,
    GatewayIntentBits.GuildEmojisAndStickers,
    GatewayIntentBits.GuildVoiceStates,
    GatewayIntentBits.GuildPresences,
    GatewayIntentBits.GuildMessages,
    GatewayIntentBits.MessageContent,
    GatewayIntentBits.GuildScheduledEvents,
  ],
});

const commandHandler = new CommandHandler({
  commandsDir: path.join(__dirname, "commands"),
});

const guildId = config.get<Snowflake>("discord.guildId");
await commandHandler.registerCommands(client, guildId);

roleSectionCache.register(
  client,
  guildId,
  RegExp(config.get<string>("roles.sectionPattern")),
);

client.once("ready", (c) => {
  logger.info(`Connected as ${c.user?.username}`);
});

await client.login(config.get("discord.token"));
