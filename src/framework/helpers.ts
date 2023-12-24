import { EmbedBuilder, InteractionReplyOptions } from "discord.js";

export function errorMessage(
  description: string,
  ephemeral = true,
): InteractionReplyOptions {
  return {
    embeds: [
      new EmbedBuilder()
        .setColor("Red")
        .setTitle("Error")
        .setDescription(description)
        .setTimestamp(Date.now()),
    ],
    ephemeral,
  };
}
