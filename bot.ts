import "dotenv/config";
import { Client, Events, GatewayIntentBits } from "discord.js";
import pino from "pino";

const logger = pino({
  transport: {
    target: "pino-pretty",
  },
});

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

client.once(Events.ClientReady, (c) => {
  logger.info(`Connected as ${c.user?.username}`);
});

await client.login(process.env.DISCORD_TOKEN);
