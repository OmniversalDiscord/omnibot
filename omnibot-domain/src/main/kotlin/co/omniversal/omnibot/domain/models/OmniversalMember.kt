package co.omniversal.omnibot.domain.models

import co.omniversal.omnibot.domain.models.roles.ColorRole
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role

// We break so many DDD rules this way but it's ok for now
class OmniversalMember(
    member: Member,
    color: ColorRole? = null
) : Member by member {
    private var roles: MutableList<Role> = member.roles

    // Ugly, ugly hack
    override fun getRoles(): MutableList<Role> {
        return roles.toMutableList()
    }

    val isCoffeeCrew = member.roles.any { it.name == "Coffee Crew" }

    var color: ColorRole? = color
        set(value) {
            val previousColor = field
            field = value
            if (previousColor != null) {
                roles.remove(previousColor)
            }
            if (value != null) {
                roles.add(value)
            }
        }

    fun clearColor() {
        color = null
    }
}