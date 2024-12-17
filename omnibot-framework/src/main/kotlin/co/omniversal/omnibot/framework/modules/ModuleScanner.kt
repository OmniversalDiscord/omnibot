package co.omniversal.omnibot.framework.modules

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner
import org.springframework.context.annotation.Configuration

private val logger = KotlinLogging.logger { }

@Configuration
@ConfigurationProperties(prefix = "omnibot.modules")
data class EnabledModules(val enabled: List<String>)

@Configuration("moduleScanner")
class ModuleScanner(
    applicationContext: ApplicationContext,
    enabledModules: EnabledModules
) {
    init {
        val registry = applicationContext.autowireCapableBeanFactory as BeanDefinitionRegistry
        val scanner = ClassPathBeanDefinitionScanner(registry)
        logger.info { "Found ${enabledModules.enabled.size} enabled modules: ${enabledModules.enabled.joinToString(", ")}" }
        enabledModules.enabled.forEach {
            val packageName = it.lowercase().replace("-", "")
            scanner.scan("co.omniversal.omnibot.bot.modules.$packageName")
        }
    }
}