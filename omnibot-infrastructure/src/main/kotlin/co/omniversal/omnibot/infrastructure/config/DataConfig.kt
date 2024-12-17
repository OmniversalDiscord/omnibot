package co.omniversal.omnibot.infrastructure.config

import com.zaxxer.hikari.HikariDataSource
import io.github.freya022.botcommands.api.core.db.HikariSourceSupplier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataConfig {
    @Bean("bcSourceSupplier")
    fun databaseSource(dataSource: HikariDataSource) =
        object : HikariSourceSupplier {
            override val source = dataSource
        }
}