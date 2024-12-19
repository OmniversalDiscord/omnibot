package co.omniversal.omnibot.bot.modules.color.commands

import co.omniversal.omnibot.bot.modules.color.components.ColorPicker
import co.omniversal.omnibot.infrastructure.data.repositories.OmniversalMemberRepository
import dev.minn.jda.ktx.coroutines.await
import io.github.freya022.botcommands.api.commands.annotations.Command
import io.github.freya022.botcommands.api.commands.application.ApplicationCommand
import io.github.freya022.botcommands.api.commands.application.slash.GuildSlashEvent
import io.github.freya022.botcommands.api.commands.application.slash.annotations.JDASlashCommand
import io.github.freya022.botcommands.api.core.utils.replaceWith
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger { }

@Command
class ColorCommand(
    private val memberRepository: OmniversalMemberRepository,
    private val colorPicker: ColorPicker
) : ApplicationCommand() {
    @JDASlashCommand(name = "color", description = "Set or clear your color role")
    suspend fun colorCommand(event: GuildSlashEvent) {
        val member = memberRepository.fromDiscordMember(event.member)

        colorPicker.sendPicker(
            event, member,
            onColorPicked = {
                member.color = it
                memberRepository.update(member)
                logger.info { "Set ${event.user.name}'s color to ${it.name}" }
                event.hook.replaceWith("Your color is now ${it.asMention}").await()
            },
            onClear = {
                member.clearColor()
                memberRepository.update(member)
                logger.info { "Cleared ${event.user.name}'s color" }
                event.hook.replaceWith("Your color has been cleared").await()
            })
    }
}