plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "co.omniversal.omnibot"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(project(":omnibot-domain"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("net.dv8tion:JDA:5.2.1")
    implementation("io.github.freya022:BotCommands:ce6c5998bc")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")
    implementation("com.h2database:h2:2.3.232")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("org.flywaydb:flyway-core:9.3.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}