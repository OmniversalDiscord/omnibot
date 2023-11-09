import { SlashCommandBuilder } from "discord.js";
import type { ChatInputCommandInteraction } from "discord.js";

export function setup(): SlashCommandBuilder {
  return new SlashCommandBuilder()
    .setName("ping")
    .setDescription("Replies with pong");
}

export default async function ping(interaction: ChatInputCommandInteraction) {
  await interaction.reply("Pong!");
}
