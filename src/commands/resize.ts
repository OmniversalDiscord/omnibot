import {
  CommandBuilder,
  Context,
  GuardData,
  guardError,
  guardOk,
  GuardResult,
  RegistrationContext,
} from "../framework/types.ts";
import config from "config";
import {
  APIApplicationCommandOptionChoice,
  DiscordAPIError,
  EmbedBuilder,
  GuildChannel,
  GuildMember,
  Routes,
  SlashCommandBuilder,
  Snowflake,
  VoiceChannel,
} from "discord.js";
import { logger } from "../logger.ts";

export async function register({
  restClient,
}: RegistrationContext): Promise<CommandBuilder> {
  // Get all resizable channels
  let channelIds = config.get<Snowflake[]>("commands.resize.channels");
  let channelOptions: APIApplicationCommandOptionChoice<string>[] = [];

  for (let id of channelIds) {
    try {
      // Just extract the type and name
      let channel = (await restClient.get(Routes.channel(id))) as {
        type: number;
        name: string;
      };

      // Voice channels are type 2
      if (channel.type !== 2) {
        logger.warn(`\`${channel.name}\` is not a voice channel, skipping...`);

        continue;
      }

      logger.debug(`Adding \`${channel.name}\` to resize...`);

      channelOptions.push({
        name: channel.name,
        value: id,
      });
    } catch (e) {
      if (e instanceof DiscordAPIError && e.code === 10003) {
        logger.warn(`Channel with ID \`${id}\` does not exist, skipping...`);
      } else {
        throw e;
      }
    }
  }

  return new SlashCommandBuilder()
    .setName("resize")
    .setDescription("Resize a channel")
    .addStringOption((option) =>
      option
        .setName("channel")
        .setDescription("Channel to resize")
        .setRequired(true)
        .addChoices(...channelOptions),
    )
    .addIntegerOption((option) =>
      option
        .setName("size")
        .setDescription("New size for the channel")
        .setMinValue(1)
        .setMaxValue(99)
        .setRequired(true),
    );
}

export function guard({ interaction }: Context) {
  // Check if the user is either in the channel or is an admin
  let member = interaction.member! as GuildMember;
  let channelId = interaction.options.getString("channel", true);
  let channel = interaction.guild?.channels.cache.get(
    channelId,
  ) as VoiceChannel;
  if (
    !channel.members.has(member.user.id) &&
    !member.roles.cache.some((r) => r.name === "Coffee Crew")
  ) {
    return guardError(`You must be in ${channel.name} to use this command`);
  }

  let newSize = interaction.options.getInteger("size", true);
  if (channel.members.size > newSize) {
    return guardError(
      `You cannot resize \`${channel.name}\` to be smaller than the current number of members`,
    );
  }

  return guardOk({ channel, newSize });
}

export default async function resize({ fromGuard, interaction }: Context) {
  let { channel, newSize } = fromGuard as GuardData<typeof guard>;

  await channel.edit({ userLimit: newSize });
  await interaction.reply({
    embeds: [
      new EmbedBuilder()
        .setTitle("Channel resized")
        .setDescription(
          `Resized \`${channel.name}\` to ${newSize} member${
            newSize === 1 ? "" : "s"
          }`,
        )
        .setColor("Blue")
        .setTimestamp(Date.now()),
    ],
  });
}
