import {
  CommandBuilder,
  Context,
  GuardData,
  guardError,
  guardOk,
  RegistrationContext,
} from "../../framework/types.ts";
import config from "config";
import {
  APIApplicationCommandOptionChoice,
  DiscordAPIError,
  EmbedBuilder,
  GuildMember,
  Routes,
  SlashCommandBuilder,
  Snowflake,
  VoiceChannel,
} from "discord.js";
import { logger } from "../../logger.ts";

export async function register({
  restClient,
}: RegistrationContext): Promise<CommandBuilder> {
  // Get all resizable channels
  const channelIds = config.get<Snowflake[]>("commands.resize.channels");
  const channelOptions: APIApplicationCommandOptionChoice<string>[] = [];

  for (let id of channelIds) {
    try {
      // Just extract the type and name
      const channel = (await restClient.get(Routes.channel(id))) as {
        type: number;
        name: string;
      };

      // Voice channels are type 2
      if (channel.type !== 2) {
        logger.warn(`\`${channel.name}\` is not a voice channel, skipping...`);

        continue;
      }

      channelOptions.push({
        name: channel.name,
        value: id,
      });

      logger.debug(`Added \`${channel.name}\` to resize options`);
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
  // Get the member and channel
  const member = interaction.member! as GuildMember;
  const channelId = interaction.options.getString("channel", true);
  const channel = interaction.guild?.channels.cache.get(
    channelId,
  ) as VoiceChannel;

  // Check if the user is either in the channel or is an admin
  if (
    !channel.members.has(member.user.id) &&
    !member.roles.cache.some((r) => r.name === "Coffee Crew")
  ) {
    return guardError(`You must be in ${channel.name} to use this command`);
  }

  // Check if the new size is smaller than the current number of members
  const newSize = interaction.options.getInteger("size", true);
  if (channel.members.size > newSize) {
    return guardError(
      `You cannot resize \`${channel.name}\` to be smaller than the current number of members`,
    );
  }

  return guardOk({ channel, newSize });
}

export default async function resize({ fromGuard, interaction }: Context) {
  const { channel, newSize } = fromGuard as GuardData<typeof guard>;

  await channel.edit({ userLimit: newSize });

  logger.debug(`Resized ${channel.name} to ${newSize} members`);
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
