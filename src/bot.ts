import "dotenv/config";
import { Client, Events, GatewayIntentBits } from "discord.js";
import { logger } from "./logger.ts";
import { CommandHandler } from "./CommandHandler.ts";

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

const commandHandler = new CommandHandler({});
await commandHandler.registerCommands(client, "342309270139830272");

client.once(Events.ClientReady, (c) => {
  logger.info(`Connected as ${c.user?.username}`);
});

await client.login(process.env.DISCORD_TOKEN);
