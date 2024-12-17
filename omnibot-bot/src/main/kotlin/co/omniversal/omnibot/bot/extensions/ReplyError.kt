package co.omniversal.omnibot.bot.extensions

import dev.minn.jda.ktx.messages.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import java.time.Instant

fun GenericCommandInteractionEvent.replyError(message: String) =
    replyEmbeds(EmbedBuilder {
        color = 16711680
        title = "Error"
        description = message
        timestamp = Instant.now()
    }.build()).setEphemeral(true)