package co.omniversal.omnibot.framework.messageactions.config

import co.omniversal.omnibot.framework.messageactions.MessageAction
import co.omniversal.omnibot.framework.messageactions.MessageActionHandler
import co.omniversal.omnibot.framework.messageactions.MessageActionType
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.getBeansOfType
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

private val logger = KotlinLogging.logger {}

@Configuration
@DependsOn("moduleScanner") // We need to scan for modules first
internal class MessageActionConfig {
    private fun getActionsByType(context: ApplicationContext, type: MessageActionType) =
        context.getBeansOfType<MessageActionHandler>().values.toList().let { actions ->
            actions.filter {
                it.javaClass.getAnnotation(MessageAction::class.java).actionType == type
            }
        }

    @Bean("moderationActions")
    fun moderationActions(context: ApplicationContext): List<MessageActionHandler> {
        val moderationActions = getActionsByType(context, MessageActionType.MODERATION)
        logger.info {
            "Found ${moderationActions.size} moderation ${if (moderationActions.size == 1) "action" else "actions"}"
        }

        return moderationActions
    }

    @Bean("deleteActions")
    fun deleteActions(context: ApplicationContext): List<MessageActionHandler> {
        val deleteActions = getActionsByType(context, MessageActionType.DELETE)
        logger.info {
            "Found ${deleteActions.size} delete ${if (deleteActions.size == 1) "action" else "actions"}"
        }

        return deleteActions
    }

    @Bean("replyActions")
    fun replyActions(context: ApplicationContext): List<MessageActionHandler> {
        val replyActions = getActionsByType(context, MessageActionType.REPLY)
        logger.info {
            "Found ${replyActions.size} reply ${if (replyActions.size == 1) "action" else "actions"}"
        }

        return replyActions
    }
}