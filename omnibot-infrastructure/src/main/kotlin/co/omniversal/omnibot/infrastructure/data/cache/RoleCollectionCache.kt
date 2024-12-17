package co.omniversal.omnibot.infrastructure.data.cache

import kotlinx.coroutines.sync.Mutex
import net.dv8tion.jda.api.entities.Role
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class RoleCollectionCache {
    /**
     * Do not use this directly. Use [getCollection] or [addRole].
     */
    val collections: MutableMap<KType, MutableSet<Role>> = mutableMapOf()

    val mutex: Mutex = Mutex()

    inline fun <reified T : Role> getCollection(): Set<T> {
        @Suppress("UNCHECKED_CAST") // Because we know it is the right type
        return collections.getOrPut(typeOf<T>()) { mutableSetOf() } as Set<T>
    }

    suspend inline fun <reified T : Role> addRole(role: T) {
        mutex.lock()
        collections.getOrPut(typeOf<T>()) { mutableSetOf() }.add(role)
        mutex.unlock()
    }

    fun clear() {
        collections.clear()
    }
}