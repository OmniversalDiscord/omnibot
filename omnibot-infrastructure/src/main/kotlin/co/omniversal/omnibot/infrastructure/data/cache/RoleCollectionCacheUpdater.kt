package co.omniversal.omnibot.infrastructure.data.cache

import co.omniversal.omnibot.domain.models.roles.ColorRole
import co.omniversal.omnibot.infrastructure.config.RolesConfig
import co.omniversal.omnibot.infrastructure.services.GuildService
import io.github.freya022.botcommands.api.core.annotations.BEventListener
import io.github.oshai.kotlinlogging.KotlinLogging
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.role.RoleDeleteEvent
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent
import net.dv8tion.jda.api.events.role.update.RoleUpdatePositionEvent
import org.springframework.stereotype.Service
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType

private val logger = KotlinLogging.logger {}

@Service
internal class RoleCollectionCacheUpdater(
    private val cache: RoleCollectionCache,
    private val config: RolesConfig,
    private val guildService: GuildService,
) {
    @BEventListener
    fun onGuildReady(event: GuildReadyEvent) = updateCache()

    @BEventListener
    fun onRoleDeleted(event: RoleDeleteEvent) = updateCache()

    @BEventListener
    fun onRolePositionUpdated(event: RoleUpdatePositionEvent) = updateCache()

    @BEventListener
    fun onRoleNameUpdated(event: RoleUpdateNameEvent) = updateCache()

    private fun updateCache() {
        cache.clear()

        var currentSectionType: KType? = null

        for (role in guildService.guild.roles.sortedByDescending { it.position }) {
            val sectionHeaderMatch = config.sectionPattern.matchEntire(role.name)
            if (sectionHeaderMatch != null) {
                val sectionName = sectionHeaderMatch.groupValues[1]
                currentSectionType = when (sectionName.lowercase()) {
                    config.colorSection.lowercase() -> ColorRole::class.starProjectedType
                    else -> null
                }
            } else {
                when (currentSectionType) {
                    ColorRole::class.starProjectedType -> {
                        cache.addRole(ColorRole(role))
                    }

                    else -> {}
                }
            }
        }

        logger.info { "Updated role cache, found ${cache.getCollection<ColorRole>().size} color roles" }
    }
}