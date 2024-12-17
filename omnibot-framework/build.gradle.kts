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
    implementation("org.springframework.boot:spring-boot-starter:3.4.0")
    implementation("net.dv8tion:JDA:5.2.1")
    implementation("io.github.freya022:BotCommands:ce6c5998bc")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}