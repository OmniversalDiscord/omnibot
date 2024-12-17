package co.omniversal.omnibot.infrastructure.services

import io.github.freya022.botcommands.api.core.BContext
import io.github.oshai.kotlinlogging.KotlinLogging
import net.dv8tion.jda.api.entities.Guild
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
internal class GuildService(
    private val context: BContext,
    @Value("\${omnibot.guild}") private val guildId: String
) {
    val guild: Guild by lazy {
        val guild = context.jda.getGuildById(guildId)
            ?: throw IllegalArgumentException("Could not find guild with id $guildId")

        logger.info { "Loaded guild ${guild.name}" }
        guild
    }
}