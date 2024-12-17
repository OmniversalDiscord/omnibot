package co.omniversal.omnibot.bot.modules.slashcommandfail.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URL

@ConfigurationProperties(prefix = "omnibot.modules.slash-command-fail")
data class SlashCommandFailConfig(val gifs: List<URL>)
