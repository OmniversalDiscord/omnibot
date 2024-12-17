package co.omniversal.omnibot.framework.messageactions.listener

import co.omniversal.omnibot.framework.messageactions.MessageActionHandler
import io.github.freya022.botcommands.api.core.annotations.BEventListener
import io.github.oshai.kotlinlogging.KotlinLogging
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger { }

@Service
@DependsOn("moduleScanner") // We need to scan for modules first
class MessageActionListener(
    private val moderationActions: List<MessageActionHandler>,
    private val deleteActions: List<MessageActionHandler>,
    private val replyActions: List<MessageActionHandler>
) {
    @BEventListener
    suspend fun onMessage(messageEvent: MessageReceivedEvent) {
        val message = messageEvent.message
        if (messageEvent.author == messageEvent.jda.selfUser) {
            logger.debug { "Ignoring message from self: ${message.contentDisplay}" }
            return
        }

        logger.info { "Checking message against actions: (${message.author.name}) ${message.contentDisplay}" }

        // First try moderation actions
        moderationActions.firstOrNull { it.handles(message) }?.let {
            logger.info { "Found moderation handler: ${it::class.simpleName}" }
            it.execute(message)
            return
        }

        // If no moderation action was found, then try delete actions
        deleteActions.firstOrNull { it.handles(message) }?.let {
            logger.info { "Found delete handler: ${it::class.simpleName}" }
            it.execute(message)
            return
        }

        // If no delete action was found, execute all matching reply actions
        var handled = false
        replyActions.filter { it.handles(message) }.forEach {
            logger.info { "Found reply handler: ${it::class.simpleName}" }
            it.execute(message)
            handled = true
        }

        if (!handled) {
            logger.info { "No handlers found for message" }
        }
    }
}