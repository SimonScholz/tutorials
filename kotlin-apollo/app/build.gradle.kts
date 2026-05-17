plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.jvm)
    id("com.apollographql.apollo") version "4.4.3"

    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.apollographql.apollo:apollo-runtime:4.4.3")
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

apollo {
    service("service") {
        packageName.set("dev.simonscholz")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "dev.simonscholz.AppKt"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
