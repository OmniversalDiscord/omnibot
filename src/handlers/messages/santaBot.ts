import { Message } from "discord.js";

// Replicates the function of santa bot
export default async function santaBot(message: Message) {
  // Filter messages to only those sent by members with the Santa role
  if (!message.member?.roles.cache.some((role) => role.name === "Santa")) {
    return;
  }

  // Confirm message contains hohoho
  if (message.content.toLowerCase().includes("hohoho")) {
    return;
  }

  // If not, delete and send message
  await message.delete();
  await message.channel.send(
    `${message.member} You have been jollyfied! You must include 'hohoho' in your message!`,
  );
}
