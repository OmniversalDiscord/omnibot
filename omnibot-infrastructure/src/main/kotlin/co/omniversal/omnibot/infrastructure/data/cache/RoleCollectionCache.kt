package co.omniversal.omnibot.infrastructure.data.cache

import net.dv8tion.jda.api.entities.Role
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class RoleCollectionCache {
    /**
     * Do not use this directly. Use [getCollection] or [addRole].
     */
    val collections: MutableMap<KType, MutableSet<Role>> = mutableMapOf()

    inline fun <reified T : Role> getCollection(): Set<T> {
        @Suppress("UNCHECKED_CAST") // Because we know it is the right type
        return collections.getOrPut(typeOf<T>()) { mutableSetOf() } as Set<T>
    }

    inline fun <reified T : Role> addRole(role: T) {
        collections.getOrPut(typeOf<T>()) { mutableSetOf() }.add(role)
    }

    fun clear() {
        collections.clear()
    }
}