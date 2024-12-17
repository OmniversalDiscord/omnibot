package co.omniversal.omnibot.bot

import io.github.freya022.botcommands.api.core.JDAService
import io.github.freya022.botcommands.api.core.config.JDAConfiguration
import io.github.freya022.botcommands.api.core.events.BReadyEvent
import io.github.freya022.botcommands.api.core.light
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.IEventManager
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.stereotype.Service

@SpringBootApplication
@ComponentScan(
    basePackages = ["co.omniversal.omnibot"],
    excludeFilters = [
        ComponentScan.Filter(type = FilterType.REGEX, pattern = ["co.omniversal.omnibot.bot.modules.*"])
    ]
)
class OmniBotApplication

@Service
class OmniBot(
    configuration: JDAConfiguration,
    @Value("\${omnibot.token}") val token: String
) : JDAService() {
    override val intents: Set<GatewayIntent> = configuration.intents

    override val cacheFlags: Set<CacheFlag> = configuration.cacheFlags

    override fun createJDA(event: BReadyEvent, eventManager: IEventManager) {
        light(
            token,
            memberCachePolicy = MemberCachePolicy.ONLINE,
            activity = Activity.watching("You.")
        )
    }
}

fun main(args: Array<String>) {
    runApplication<OmniBotApplication>(*args)
}
