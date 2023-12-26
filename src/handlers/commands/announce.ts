import {
  CommandBuilder,
  Context,
  GuardData,
  guardError,
  guardOk,
} from "../../framework/types.ts";
import {
  ActionRowBuilder,
  EmbedBuilder,
  ModalBuilder,
  PermissionsBitField,
  Role,
  SlashCommandBuilder,
  Snowflake,
  TextChannel,
  TextInputBuilder,
  TextInputComponent,
  TextInputStyle,
} from "discord.js";
import { roleSectionCache } from "../../models/roleSectionCache.ts";
import config from "config";
import { randomUUID } from "crypto";
import { logger } from "../../logger.ts";

export const register = (): CommandBuilder =>
  new SlashCommandBuilder()
    .setName("announce")
    .setDescription("Post an event announcement")
    .setDefaultMemberPermissions(PermissionsBitField.Flags.Administrator)
    .addRoleOption((option) =>
      option.setName("ping").setDescription("Role to ping").setRequired(true),
    );

export function guard({ interaction }: Context) {
  // Get announcement channel
  const announcementChannel = interaction.guild?.channels.cache.get(
    config.get<Snowflake>("commands.announce.channel"),
  );

  if (!announcementChannel?.isTextBased()) {
    throw new Error("Announcement channel is not a text channel");
  }

  // Make sure the role is a pingable role
  const role = interaction.options.getRole("ping", true) as Role;
  const pingableRoles = roleSectionCache.get(
    config.get<string>("commands.announce.roleSection"),
  );

  if (!pingableRoles?.some((pingableRole) => pingableRole.id === role.id)) {
    return guardError(`Announcement pings are not enabled for ${role}`);
  }

  return guardOk({ channel: announcementChannel as TextChannel, role });
}

export default async function announce({ fromGuard, interaction }: Context) {
  const { channel, role } = fromGuard as GuardData<typeof guard>;
  const uuid = randomUUID(); // We need a random ID to identify the modal

  // Create a modal for the user to enter the announcement
  const modal = new ModalBuilder()
    .setTitle("Announcement")
    .setCustomId(`modal-${uuid}`)
    .addComponents(
      new ActionRowBuilder<TextInputBuilder>().addComponents(
        new TextInputBuilder()
          .setCustomId("announcement")
          .setLabel("Message")
          .setRequired(true)
          .setStyle(TextInputStyle.Paragraph),
      ),
    );

  logger.debug(`Sent an announcement modal to ${interaction.user.username}`);
  await interaction.showModal(modal);

  try {
    const response = await interaction.awaitModalSubmit({
      filter: (i) => i.customId === `modal-${uuid}`,
      time: 5 * 60 * 1000, // 5 minutes to type an announcement
    });

    // Get the message from the modal
    const message = (response.components[0].components[0] as TextInputComponent)
      .value;

    logger.debug(`Pinging ${role} in ${channel.name} with \n${message}`);
    await channel.send(`${role} ${message}`); // Send the announcement

    // Send a confirmation message to the user
    await response.reply({
      embeds: [
        new EmbedBuilder()
          .setTitle("Sent an announcement")
          .setDescription(`${role} ${message}`)
          .setColor("Blue")
          .setTimestamp(Date.now()),
      ],
    });
  } catch (e) {
    return; // User didn't respond in time or cancelled (no way to tell which)
  }
}
