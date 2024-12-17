plugins {
    kotlin("jvm") version "2.1.0"
}

group = "co.omniversal.omnibot"
version = "unspecified"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.data:spring-data-commons:3.4.0")

    implementation("net.dv8tion:JDA:5.2.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}