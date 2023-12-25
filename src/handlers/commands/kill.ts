import { SlashCommandBuilder } from "discord.js";
import { CommandBuilder, Context } from "../../framework/types.ts";

export const register = (): CommandBuilder =>
  new SlashCommandBuilder()
    .setName("kill")
    .setDescription("Kills a user (scary!)")
    .addUserOption((option) =>
      option
        .setName("user")
        .setDescription("The user to kill")
        .setRequired(true),
    );

export default async function kill({ interaction }: Context) {
  const user = interaction.options.getUser("user", true);
  await interaction.reply(
    `${interaction.user} has been charged with the attempted murder of ${user} and is awaiting sentencing.`,
  );
}
