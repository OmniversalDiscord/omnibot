import { Client, Message } from "discord.js";
import fs from "fs";
import { logger } from "../logger.ts";

type HandlerFn = (message: Message) => Promise<void>;

type MessageHandlerOptions = {
  handlerDir: string;
};

export class MessageHandlerFramework {
  handlerDir: string;
  handlers: HandlerFn[] = [];

  constructor(options: MessageHandlerOptions) {
    this.handlerDir = options.handlerDir;
  }

  public async registerHandlers(client: Client) {
    const handlerFiles = await fs.promises.readdir(this.handlerDir);

    // Used to log all the handlers that were registered
    const handlerNames = [];

    for (const file of handlerFiles) {
      const handler = await import(`${this.handlerDir}/${file}`);
      const handlerName = file.split(".")[0];

      if (!handler.default) {
        logger.warn(
          `Message handler ${handlerName} has no default function defined, skipping...`,
        );
        continue;
      }

      this.handlers.push(handler.default);
      handlerNames.push(handlerName);
    }

    logger.info(`Registered message handlers: ${handlerNames.join(", ")}`);

    client.on("messageCreate", this.handleMessage.bind(this));
  }

  public async handleMessage(message: Message) {
    for (const handler of this.handlers) {
      await handler(message);
    }
  }
}
