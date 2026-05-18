plugins {
    alias(libs.plugins.kotlin.jvm)
    id("com.apollographql.apollo") version "5.0.0"

    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.apollographql.apollo:apollo-runtime:5.0.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")
}

apollo {
    service("service") {
        packageName.set("dev.simonscholz")
        introspection {
            schemaFile.set(file("src/main/graphql/schema.graphqls"))
        }
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
