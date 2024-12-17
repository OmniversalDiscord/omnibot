package co.omniversal.omnibot.infrastructure.config

import co.omniversal.omnibot.domain.models.roles.ColorRole
import co.omniversal.omnibot.infrastructure.data.cache.RoleCollectionCache
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConfigurationProperties("omnibot.roles")
internal data class RolesConfig(val sectionPattern: Regex, val colorSection: String)

@Configuration
@EnableConfigurationProperties(RolesConfig::class)
internal class RoleCollectionConfig {
    @Bean
    fun roleCollectionCache(): RoleCollectionCache {
        return RoleCollectionCache()
    }

    @Bean
    fun colorRoles(cache: RoleCollectionCache): Set<ColorRole> {
        return cache.getCollection<ColorRole>()
    }
}