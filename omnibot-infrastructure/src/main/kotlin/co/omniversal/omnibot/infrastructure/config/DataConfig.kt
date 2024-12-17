package co.omniversal.omnibot.infrastructure.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.freya022.botcommands.api.core.db.HikariSourceSupplier
import org.flywaydb.core.Flyway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.seconds

@Configuration
class DataConfig {
    @Bean("bcSourceSupplier")
    fun databaseSource(): HikariSourceSupplier {
        val supplier = object : HikariSourceSupplier {
            override val source = HikariDataSource(HikariConfig().apply {
                jdbcUrl = "jdbc:h2:mem:omnibot;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
                maximumPoolSize = 2
                leakDetectionThreshold = 10.seconds.inWholeMilliseconds
            })
        }

        Flyway.configure()
            .dataSource(supplier.source)
            .schemas("bc")
            .locations("bc_database_scripts")
            .validateMigrationNaming(true)
            .loggers("slf4j")
            .load()
            .migrate()

        return supplier
    }
}