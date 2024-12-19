package co.omniversal.omnibot.infrastructure.data.repositories

import co.omniversal.omnibot.domain.models.OmniversalMember
import co.omniversal.omnibot.domain.models.UpdateRepository
import co.omniversal.omnibot.domain.models.roles.ColorRole
import co.omniversal.omnibot.infrastructure.data.cache.RoleCollectionCache
import co.omniversal.omnibot.infrastructure.services.GuildService
import dev.minn.jda.ktx.coroutines.await
import net.dv8tion.jda.api.entities.Member
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Repository

@Repository
@Lazy
class OmniversalMemberRepository
internal constructor(
    private val guildService: GuildService,
    private val roles: RoleCollectionCache
) : UpdateRepository<OmniversalMember, String> {
    override suspend fun findById(id: String): OmniversalMember? {
        return guildService.guild.getMemberById(id)?.let { fromDiscordMember(it) }
    }

    suspend fun fromDiscordMember(member: Member): OmniversalMember {
        val colorRoles = roles.getCollection<ColorRole>()
        val roleSet = member.roles.map { it.id }.toSet()
        val color = colorRoles.firstOrNull { roleSet.contains(it.id) }

        return OmniversalMember(member, color)
    }

    override suspend fun <S : OmniversalMember> update(entity: S): S {
        val member = guildService.guild.getMemberById(entity.id)
            ?: throw IllegalStateException("Attempted to save a member that doesn't exist: $entity")

        guildService.guild.modifyMemberRoles(member, entity.roles).await()

        return entity
    }
}