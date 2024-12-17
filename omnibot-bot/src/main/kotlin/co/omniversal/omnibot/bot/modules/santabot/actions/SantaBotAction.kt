package co.omniversal.omnibot.bot.modules.santabot.actions

import co.omniversal.omnibot.framework.messageactions.MessageAction
import co.omniversal.omnibot.framework.messageactions.MessageActionHandler
import co.omniversal.omnibot.framework.messageactions.MessageActionType
import dev.minn.jda.ktx.coroutines.await
import io.github.oshai.kotlinlogging.KotlinLogging
import net.dv8tion.jda.api.entities.Message

private val logger = KotlinLogging.logger {}

@MessageAction(MessageActionType.DELETE)
class SantaBotAction : MessageActionHandler {
    override suspend fun handles(message: Message): Boolean {
        val member = message.member ?: return false

        val shouldDelete = member.roles.any { it.name == "Santa" }
                && !message.contentRaw.contains("hohoho", ignoreCase = true)

        if (shouldDelete) {
            logger.info { "${member.user.name} is santa and message is not jolly, deleting message" }
        } else {
            logger.info { "${member.user.name} is not santa or message is jolly, not deleting message" }
        }
        return shouldDelete
    }

    override suspend fun execute(message: Message) {
        message.delete().reason("Jollyfied").await()
        message.channel
            .sendMessage("${message.author.asMention} You have been jollyfied! You must include \"hohoho\" in your message!")
            .await()
    }
}