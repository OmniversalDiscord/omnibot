package co.omniversal.omnibot.bot.modules.generate.commands

import co.omniversal.omnibot.bot.extensions.replyError
import dev.minn.jda.ktx.coroutines.await
import io.github.freya022.botcommands.api.commands.annotations.Command
import io.github.freya022.botcommands.api.commands.application.ApplicationCommand
import io.github.freya022.botcommands.api.commands.application.slash.GuildSlashEvent
import io.github.freya022.botcommands.api.commands.application.slash.annotations.JDASlashCommand
import io.github.freya022.botcommands.api.commands.application.slash.annotations.SlashOption

@Command
class GenerateCommand : ApplicationCommand() {
    /**
     * Joke command that does nothing except tell the user to purchase a fictional membership
     */
    @JDASlashCommand(name = "generate", description = "Generates images based on a given prompt")
    suspend fun generateCommand(
        event: GuildSlashEvent,
        @SlashOption(name = "prompt", description = "The prompt to generate an image for") prompt: String
    ) {
        event.replyError("This command requires Omniversal+ membership!")
            .setEphemeral(false) // Override the ephemeral flag that replyError sets
            .await()
    }
}
