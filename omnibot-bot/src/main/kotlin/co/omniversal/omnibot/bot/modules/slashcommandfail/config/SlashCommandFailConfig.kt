package co.omniversal.omnibot.bot.modules.slashcommandfail.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.net.URL

@Configuration
@ConfigurationProperties(prefix = "omnibot.modules.slash-command-fail")
data class SlashCommandFailConfig(val gifs: List<URL>)
