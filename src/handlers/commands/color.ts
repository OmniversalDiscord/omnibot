import { CommandBuilder, Context } from "../../framework/types.ts";
import {
  ActionRowBuilder,
  ButtonBuilder,
  ButtonStyle,
  EmbedBuilder,
  GuildMember,
  Message,
  SelectMenuBuilder,
  SlashCommandBuilder,
  StringSelectMenuBuilder,
  StringSelectMenuOptionBuilder,
} from "discord.js";
import { roleSectionCache } from "../../models/roleSectionCache.ts";
import { UserColorService } from "../../services/userColorService.ts";
import { logger } from "../../logger.ts";
import config from "config";

export const register = (): CommandBuilder =>
  new SlashCommandBuilder()
    .setName("color")
    .setDescription("Set or clear your color role");

function createColorPickerMessage(userColorService: UserColorService) {
  const colors = userColorService.colors;
  const roleMentionList = colors.join("\n");
  const embed = new EmbedBuilder()
    .setTitle("Available colors")
    .setDescription(roleMentionList)
    .setFooter({
      text: "Use the select meu below to choose your color in chat",
    })
    .setColor("Blue");

  const currentColor = userColorService.currentColor;
  const selectOptions = colors.map((color) =>
    new StringSelectMenuOptionBuilder()
      .setLabel(color.name)
      .setValue(color.id)
      .setDefault(color.id === currentColor?.id),
  );

  const selectMenu = new StringSelectMenuBuilder()
    .setCustomId("color")
    .setPlaceholder("Select a color...")
    .addOptions(selectOptions);

  const selectMenuRow = new ActionRowBuilder<SelectMenuBuilder>().addComponents(
    selectMenu,
  );

  const buttonRow = new ActionRowBuilder<ButtonBuilder>().addComponents(
    new ButtonBuilder()
      .setCustomId("cancel")
      .setLabel("Cancel")
      .setStyle(ButtonStyle.Secondary),
  );

  if (currentColor) {
    buttonRow.addComponents(
      new ButtonBuilder()
        .setCustomId("clear")
        .setLabel("Clear")
        .setStyle(ButtonStyle.Danger),
    );
  }

  return {
    embeds: [embed],
    components: [selectMenuRow, buttonRow],
  };
}

async function getSelection(fromMessage: Message) {
  try {
    return await fromMessage.awaitMessageComponent({
      time: 60000,
    });
  } catch (e) {
    return null;
  }
}

export default async function color({ interaction }: Context) {
  const member = interaction.member! as GuildMember;
  const colors = roleSectionCache.get(
    config.get<string>("commands.color.roleSection"),
  );

  if (!colors) {
    throw new Error("Color command was called but no color section is defined");
  }

  const userColorService = new UserColorService(colors, member);

  const message = await interaction.reply({
    ...createColorPickerMessage(userColorService),
    fetchReply: true,
    ephemeral: true,
  });

  const selection = await getSelection(message);

  // If the user selected a color, update their color
  if (selection?.isStringSelectMenu()) {
    let newColorId = selection.values[0];

    // Update the user's color (set color checks the role is a color)
    await userColorService.setColor(newColorId);

    logger.debug(`Set user's color to ${userColorService.currentColor}`);
    return selection.update({
      content: `Your color is now ${userColorService.currentColor}`,
      embeds: [],
      components: [],
    });
  }

  // If the user clicked clear, clear their color
  if (selection?.customId === "clear") {
    await userColorService.clearColor();
    logger.debug("Cleared user color");

    return selection.update({
      content: "Your color has been cleared",
      embeds: [],
      components: [],
    });
  }

  // Otherwise, user cancelled or timed out
  logger.debug("Color picker was cancelled or timed out");
  return interaction.editReply({
    components: [],
  });
}
