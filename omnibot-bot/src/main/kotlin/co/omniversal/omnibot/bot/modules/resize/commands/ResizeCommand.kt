package co.omniversal.omnibot.bot.modules.resize.commands

import co.omniversal.omnibot.bot.extensions.replyError
import co.omniversal.omnibot.bot.filters.UserInChannelFilter
import co.omniversal.omnibot.bot.modules.resize.config.ResizeConfig
import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.Embed
import io.github.freya022.botcommands.api.commands.CommandPath
import io.github.freya022.botcommands.api.commands.annotations.Command
import io.github.freya022.botcommands.api.commands.annotations.Filter
import io.github.freya022.botcommands.api.commands.application.ApplicationCommand
import io.github.freya022.botcommands.api.commands.application.ApplicationCommandRejectionHandler
import io.github.freya022.botcommands.api.commands.application.slash.GuildSlashEvent
import io.github.freya022.botcommands.api.commands.application.slash.annotations.JDASlashCommand
import io.github.freya022.botcommands.api.commands.application.slash.annotations.LongRange
import io.github.freya022.botcommands.api.commands.application.slash.annotations.SlashOption
import io.github.oshai.kotlinlogging.KotlinLogging
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import java.time.Instant

private val logger = KotlinLogging.logger { }

@Command
class ResizeCommand(
    private val config: ResizeConfig,
    private val rejectionHandler: ApplicationCommandRejectionHandler<*>
) : ApplicationCommand() {
    override fun getOptionChoices(guild: Guild?, commandPath: CommandPath, optionName: String): List<Choice> {
        if (guild == null || commandPath.name != "resize" || optionName != "channel") {
            return super.getOptionChoices(guild, commandPath, optionName)
        }

        val channels = config.channels.mapNotNull { channelId ->
            val channel = guild.channels.find { it.id == channelId }
            if (channel == null) {
                logger.warn { "Could not find channel with id $channelId" }
                return@mapNotNull null
            } else {
                return@mapNotNull Choice(channel.name, channel.id)
            }
        }

        logger.info { "Found resizable channels: ${channels.map { it.name }}" }

        return channels
    }

    @JDASlashCommand(name = "resize", description = "Resize a channel")
    @Filter(UserInChannelFilter::class)
    suspend fun resizeCommand(
        event: GuildSlashEvent,
        @SlashOption(name = "channel", description = "The channel to resize") channelId: String,
        @SlashOption(description = "The new size of the channel") @LongRange(from = 1, to = 99) size: Int
    ) {
        rejectionHandler
        val channel = event.guild.getVoiceChannelById(channelId)
            ?: throw IllegalArgumentException("Could not find channel with id $channelId")

        if (channel.members.size > size) {
            event.replyError("You cannot resize ${channel.name} to be smaller than the current number of members")
                .await()
            return
        }

        channel.manager.setUserLimit(size).await()

        event.replyEmbeds(Embed {
            title = "Channel resized"
            description = "Resized ${channel.name} to $size ${if (size == 1) "member" else "members"}"
            color = 0x007FFF
            timestamp = Instant.now()
        }).await()
    }
}