package co.omniversal.omnibot.bot.modules.color.components

import co.omniversal.omnibot.domain.models.OmniversalMember
import co.omniversal.omnibot.domain.models.roles.ColorRole
import co.omniversal.omnibot.infrastructure.data.cache.RoleCollectionCache
import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.interactions.components.row
import dev.minn.jda.ktx.messages.EmbedBuilder
import io.github.freya022.botcommands.api.commands.application.slash.GuildSlashEvent
import io.github.freya022.botcommands.api.components.Buttons
import io.github.freya022.botcommands.api.components.SelectMenus
import io.github.freya022.botcommands.api.core.utils.replaceWith
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.utils.messages.MessageEditData
import org.springframework.stereotype.Component
import kotlin.time.Duration.Companion.seconds

@Component
class ColorPicker(
    private val buttons: Buttons,
    private val selectMenus: SelectMenus,
    private val roles: RoleCollectionCache
) {
    suspend fun sendPicker(
        event: GuildSlashEvent,
        member: OmniversalMember,
        onColorPicked: suspend (ColorRole) -> Unit,
        onClear: suspend () -> Unit
    ) {
        val colors = roles.getCollection<ColorRole>()
        val colorList = colors.joinToString("\n") { it.asMention }
        val embed = EmbedBuilder {
            title = "Pick a color"
            description = colorList
            color = 0x007FFF
            footer("Use the select menu below to choose your color in chat")
        }.build()

        val selectMenu = selectMenus.stringSelectMenu().ephemeral {
            noTimeout()
            singleUse = true
            placeholder = "Pick a color"
            addOptions(colors.map { SelectOption.of(it.name, it.id) })
            member.color?.let { setDefaultValues(it.id) }
            bindTo { event ->
                val color = colors.find { it.id == event.values[0] }
                    ?: throw IllegalStateException("User selected a color that does not exist: ${event.values[0]}")
                onColorPicked(color)
            }
        }

        val cancel = buttons.secondary("Cancel").ephemeral {
            noTimeout()
            singleUse = true
            bindTo {
                event.hook.replaceWith(MessageEditData.fromEmbeds(embed)).await()
            }
        }

        val clear = buttons.danger("Clear color").ephemeral {
            noTimeout()
            singleUse = true
            bindTo { onClear() }
        }.apply { if (member.color == null) asDisabled() }

        buttons.group(selectMenu, cancel, clear).ephemeral {
            timeout(60.seconds) {
                event.hook.replaceWith(MessageEditData.fromEmbeds(embed)).await()
            }
        }

        event.replyEmbeds(embed).addComponents(row(selectMenu), row(cancel, clear)).setEphemeral(true).await()
    }
}