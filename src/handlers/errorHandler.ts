import { Client, TextChannel } from "discord.js";
import { errorMessage } from "../framework/helpers.ts";
import { logger } from "../logger.ts";

export class ErrorHandler {
  private readonly channelId: string;
  private channel!: TextChannel;

  constructor(channelId: string) {
    this.channelId = channelId;
  }

  public register(client: Client) {
    client.once("ready", () => {
      let channel = client.channels.cache.get(this.channelId);
      if (!channel || !channel.isTextBased()) {
        throw new Error(
          "Specified error channel does not exist or is not a text channel",
        );
      }

      this.channel = channel as TextChannel;
    });

    client.on("error", this.handleError.bind(this));
  }

  async handleError(error: Error) {
    const { embeds } = errorMessage(
      `${error.message} \`\`\`${error.stack}\`\`\``,
      false,
    );
    await this.channel.send({ content: "<@131859790593785856>", embeds });
    logger.error(error);
  }
}
