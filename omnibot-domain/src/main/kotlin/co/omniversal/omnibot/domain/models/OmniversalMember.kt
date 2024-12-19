package co.omniversal.omnibot.domain.models

import co.omniversal.omnibot.domain.models.roles.ColorRole
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role

// We break so many DDD rules this way but it's ok for now
class OmniversalMember(
    member: Member,
    color: ColorRole? = null
) : Member by member {
    var roles: List<Role> = member.roles
        private set

    val isCoffeeCrew = member.roles.any { it.name == "Coffee Crew" }

    var color: ColorRole? = color
        set(value) {
            val previousColor = field
            field = value
            val newRoles = roles.toMutableList()
            if (previousColor != null) {
                newRoles.remove(previousColor)
            }
            if (value != null) {
                newRoles.add(value)
            }
            roles = newRoles
        }

    fun clearColor() {
        color = null
    }
}