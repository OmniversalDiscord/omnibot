import { SlashCommandBuilder } from "discord.js";
import {
  CommandBuilder,
  Context,
  guardError,
  guardOk,
  GuardResult,
} from "../framework/types.ts";

export function register(): CommandBuilder {
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

export function guard({ interaction }: Context): GuardResult {
  if (interaction.member!.user.id === "131859790593785856") {
    guardError("You can't kill the bot owner!");
  }

  return guardOk();
}

export default async function kill({ interaction }: Context) {
  const user = interaction.options.getUser("user", true);
  await interaction.reply(
    `<@${interaction.user}> has been charged with the attempted murder of <@${user}> and is awaiting sentencing.`,
  );
}
