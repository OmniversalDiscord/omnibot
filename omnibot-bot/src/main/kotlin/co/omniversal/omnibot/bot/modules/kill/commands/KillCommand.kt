package co.omniversal.omnibot.bot.modules.kill.commands

import dev.minn.jda.ktx.coroutines.await
import io.github.freya022.botcommands.api.commands.annotations.Command
import io.github.freya022.botcommands.api.commands.application.ApplicationCommand
import io.github.freya022.botcommands.api.commands.application.slash.GuildSlashEvent
import io.github.freya022.botcommands.api.commands.application.slash.annotations.JDASlashCommand
import io.github.freya022.botcommands.api.commands.application.slash.annotations.SlashOption
import net.dv8tion.jda.api.entities.Member
import kotlin.random.Random

@Command
class KillCommand : ApplicationCommand() {
    @JDASlashCommand(name = "kill", description = "Kills a member (scary!)")
    suspend fun killCommand(event: GuildSlashEvent, @SlashOption(description = "Member to kill") member: Member) {
        if (member.id == event.jda.selfUser.id) {
            val emoji = member.guild.getEmojiById("1009062092281745461")

            event.reply(emoji?.formatted ?: ":boar:").await()
            return
        }

        val shouldKill = Random.Default.nextInt(20) == 0

        if (shouldKill) {
            event.reply("${member.asMention} has been killed by ${event.member.asMention}! :worried:").await()
        } else {
            event.reply("${event.member.asMention} has been charged with the attempted murder of ${member.asMention} and is awaiting trial.")
                .await()
        }
    }
}