package co.omniversal.omnibot.bot.modules.slashcommandfail.actions

import co.omniversal.omnibot.bot.modules.slashcommandfail.config.SlashCommandFailConfig
import co.omniversal.omnibot.framework.messageactions.MessageAction
import co.omniversal.omnibot.framework.messageactions.MessageActionHandler
import co.omniversal.omnibot.framework.messageactions.MessageActionType
import dev.minn.jda.ktx.coroutines.await
import io.github.oshai.kotlinlogging.KotlinLogging
import net.dv8tion.jda.api.entities.Message
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

@MessageAction(MessageActionType.REPLY)
class SlashCommandFailAction(private val config: SlashCommandFailConfig) : MessageActionHandler {

    // Filter all messages not starting with / or shorter than 5 characters to avoid
    // annoying those who choose to use Tone Indicators(tm)
    override suspend fun handles(message: Message): Boolean {
        val startsWithSlash = message.contentRaw.startsWith("/")

        if (!startsWithSlash) {
            logger.info { "Message does not start with /, ignoring" }
            return false
        }

        val isLongEnough = message.contentRaw.length > 5

        if (!isLongEnough) {
            logger.info { "Message starts with slash but is too short, ignoring" }
            return false
        }

        return true
    }

    override suspend fun execute(message: Message) {
        val gif = config.gifs[Random.nextInt(config.gifs.size)]

        message.reply(gif.toString()).await()
    }
}