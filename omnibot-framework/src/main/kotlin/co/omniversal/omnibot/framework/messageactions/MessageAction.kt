package co.omniversal.omnibot.framework.messageactions

import org.springframework.stereotype.Component

enum class MessageActionType {
    MODERATION,
    DELETE,
    REPLY
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class MessageAction(val actionType: MessageActionType)
