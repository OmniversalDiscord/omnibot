import { SlashCommandBuilder } from "discord.js";
import type { ChatInputCommandInteraction } from "discord.js";
import type { CommandBuilder } from "../CommandHandler.ts";

export function build(): CommandBuilder {
  return new SlashCommandBuilder()
    .setName("kill")
    .setDescription("Kills a user (scary!)")
    .addUserOption((option) =>
      option
        .setName("user")
        .setDescription("The user to kill")
        .setRequired(true),
    );
}

export default async function kill(interaction: ChatInputCommandInteraction) {
  const user = interaction.options.getUser("user", true);
  await interaction.reply(
    `<@${interaction.user.id}> has been charged with the attempted murder of <@${user.id}> and is awaiting sentencing.`,
  );
}
