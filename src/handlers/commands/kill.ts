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

  if (user.id == interaction.client.user.id) {
    const boarEmoji = interaction.client.emojis.cache.get(
      "1009062092281745461",
    );

    return interaction.reply(`${boarEmoji ?? "🐗"}`);
  }

  await interaction.reply(
    `${interaction.user} has been charged with the attempted murder of ${user} and is awaiting sentencing.`,
  );
}
