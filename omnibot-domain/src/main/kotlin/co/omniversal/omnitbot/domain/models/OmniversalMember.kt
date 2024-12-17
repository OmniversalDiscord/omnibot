package co.omniversal.omnitbot.domain.models

import co.omniversal.omnitbot.domain.models.roles.ColorRole
import net.dv8tion.jda.api.entities.Role

class OmniversalMember(
    val id: String,
    roles: List<Role>,
    color: ColorRole?,
    val isCoffeeCrew: Boolean
) {
    var roles: List<Role> = roles
        private set

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