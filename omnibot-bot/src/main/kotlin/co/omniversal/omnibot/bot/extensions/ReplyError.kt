package co.omniversal.omnibot.bot.extensions

import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import java.time.Instant

fun GenericCommandInteractionEvent.replyError(message: String) =
    replyEmbeds(Embed {
        color = 16711680
        title = "Error"
        description = message
        timestamp = Instant.now()
    }).setEphemeral(true)