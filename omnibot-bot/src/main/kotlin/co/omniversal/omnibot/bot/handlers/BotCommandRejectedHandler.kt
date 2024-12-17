package co.omniversal.omnibot.bot.handlers

import co.omniversal.omnibot.bot.extensions.replyError
import dev.minn.jda.ktx.coroutines.await
import io.github.freya022.botcommands.api.commands.application.ApplicationCommandInfo
import io.github.freya022.botcommands.api.commands.application.ApplicationCommandRejectionHandler
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import org.springframework.stereotype.Service

@Service
class BotCommandRejectedHandler : ApplicationCommandRejectionHandler<String> {
    override suspend fun handleSuspend(
        event: GenericCommandInteractionEvent,
        commandInfo: ApplicationCommandInfo,
        userData: String
    ) {
        event.replyError(userData).await()
    }
}