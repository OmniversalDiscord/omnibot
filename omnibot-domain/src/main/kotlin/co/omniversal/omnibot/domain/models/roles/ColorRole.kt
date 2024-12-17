package co.omniversal.omnibot.domain.models.roles

import net.dv8tion.jda.api.entities.Role

/**
 * Marker class for roles that represent a member's color
 */
class ColorRole(role: Role) : Role by role