package co.omniversal.omnibot.bot.modules.resize.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "omnibot.modules.resize")
data class ResizeConfig(val channels: List<String>)