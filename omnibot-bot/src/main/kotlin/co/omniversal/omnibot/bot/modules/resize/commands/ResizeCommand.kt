package co.omniversal.omnibot.bot.modules.resize.commands

import co.omniversal.omnibot.bot.extensions.replyError
import co.omniversal.omnibot.bot.filters.UserInChannelFilter
import co.omniversal.omnibot.bot.modules.resize.config.ResizeConfig
import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.generics.getChannel
import dev.minn.jda.ktx.messages.Embed
import io.github.freya022.botcommands.api.commands.annotations.Command
import io.github.freya022.botcommands.api.commands.application.CommandScope
import io.github.freya022.botcommands.api.commands.application.ValueRange
import io.github.freya022.botcommands.api.commands.application.builder.filter
import io.github.freya022.botcommands.api.commands.application.provider.GuildApplicationCommandManager
import io.github.freya022.botcommands.api.commands.application.provider.GuildApplicationCommandProvider
import io.github.freya022.botcommands.api.commands.application.slash.GuildSlashEvent
import io.github.freya022.botcommands.api.commands.application.slash.annotations.LongRange
import io.github.freya022.botcommands.api.commands.application.slash.annotations.SlashOption
import io.github.freya022.botcommands.api.core.utils.awaitUnit
import io.github.freya022.botcommands.api.core.warnNull
import io.github.oshai.kotlinlogging.KotlinLogging
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import java.time.Instant

private val logger = KotlinLogging.logger { }

// With help from @freya022
@Command
class ResizeCommand(
    private val config: ResizeConfig,
) : GuildApplicationCommandProvider {
    suspend fun resizeCommand(
        event: GuildSlashEvent,
        @SlashOption(name = "channel", description = "The channel to resize") channelId: String,
        @SlashOption(description = "The new size of the channel") @LongRange(from = 1, to = 99) size: Int
    ) {
        val channel = event.guild.getVoiceChannelById(channelId)
            ?: throw IllegalArgumentException("Could not find channel with id $channelId")

        if (channel.members.size > size) {
            return event.replyError("You cannot resize ${channel.name} to be smaller than the current number of members")
                .awaitUnit()
        }

        channel.manager.setUserLimit(size).await()

        event.replyEmbeds(Embed {
            title = "Channel resized"
            description = "Resized ${channel.name} to $size ${if (size == 1) "member" else "members"}"
            color = 0x007FFF
            timestamp = Instant.now()
        }).await()
    }

    override fun declareGuildApplicationCommands(manager: GuildApplicationCommandManager) {
        manager.slashCommand("resize", scope = CommandScope.GUILD, function = ::resizeCommand) {
            description = "Resize a channel"

            filters += filter<UserInChannelFilter>()

            option("channelId", optionName = "channel") {
                description = "The channel to resize"

                val channelChoices = getResizableChannelChoices(manager.guild)
                logger.info { "Found resizable channels: ${channelChoices.map { it.name }}" }
                choices = channelChoices
            }

            option("size") {
                description = "The new size of the channel"

                valueRange = ValueRange.ofLong(1, 99)
            }
        }
    }

    private fun getResizableChannelChoices(guild: Guild) =
        config.channels.mapNotNull { channelId ->
            val channel = guild.getChannel<VoiceChannel>(channelId)
                ?: return@mapNotNull logger.warnNull { "Could not find channel with id $channelId" }

            Choice(channel.name, channel.id)
        }
}