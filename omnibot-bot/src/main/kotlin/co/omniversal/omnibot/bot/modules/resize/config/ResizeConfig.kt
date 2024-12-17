package co.omniversal.omnibot.bot.modules.resize.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "omnibot.modules.resize")
data class ResizeConfig(val channels: List<String>)