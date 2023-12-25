import { Message } from "discord.js";

const replyGifs = [
  "https://cdn.discordapp.com/attachments/342309270139830272/1122859045502595112/caption.gif",
  "https://cdn.discordapp.com/attachments/342309270139830272/1122859147608735834/caption.gif",
  "https://cdn.discordapp.com/attachments/342309270139830272/1122859273395908618/caption.gif",
  "https://cdn.discordapp.com/attachments/342309270139830272/1122859386671468684/caption.gif",
];

// This only exists to make fun of the user for sending a slash command as a normal message
export default async function slashCommandFail(message: Message) {
  // Filter all messages not starting with /, or those less then or equal to 4 characters long to avoid
  // annoying those who choose to use Tone Indicators(tm)
  if (!message.content.startsWith("/") || message.content.length <= 4) {
    return;
  }

  // Pick a random gif
  const gif = replyGifs[(Math.random() * replyGifs.length) | 0];
  await message.reply(gif);
}
