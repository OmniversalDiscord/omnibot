package co.omniversal.omnibot.bot.filters

import io.github.freya022.botcommands.api.commands.application.ApplicationCommandFilter
import io.github.freya022.botcommands.api.commands.application.ApplicationCommandInfo
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import org.springframework.stereotype.Service

@Service
class UserInChannelFilter : ApplicationCommandFilter<String> {
    override val global = false

    override fun check(event: GenericCommandInteractionEvent, commandInfo: ApplicationCommandInfo): String? {
        val channel = findChannelFromEvent(event)

        return if (event.member?.voiceState?.channel?.asVoiceChannel() == channel) {
            null
        } else {
            "You must be in ${channel.name} to use this command"
        }
    }

    private fun findChannelFromEvent(event: GenericCommandInteractionEvent): GuildChannel {
        for (option in event.options) {
            if (option.type == OptionType.CHANNEL) {
                return option.asChannel
            }

            if (option.type == OptionType.STRING && option.name.lowercase() == "channel") {
                return event.guild?.getVoiceChannelById(option.asString)
                    ?: throw IllegalArgumentException("Could not find a voice channel with id ${option.asString}")
            }
        }

        throw IllegalStateException("Filter was applied to a command with no channel option")
    }
}