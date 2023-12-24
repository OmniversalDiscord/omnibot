import "dotenv/config";
import { Client, Events, GatewayIntentBits, Snowflake } from "discord.js";
import { logger } from "./logger.ts";
import { CommandHandler } from "./framework/CommandHandler.ts";
import config from "config";
import path from "path";

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

await commandHandler.registerCommands(
  client,
  config.get<Snowflake>("discord.guildId"),
);

client.once(Events.ClientReady, (c) => {
  logger.info(`Connected as ${c.user?.username}`);
});

await client.login(config.get("discord.token"));
