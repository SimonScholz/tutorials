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
    service("github") {
        packageName.set("dev.simonscholz.github")
        introspection {
            schemaFile.set(file("src/main/graphql/github/schema.graphqls"))
        }
        srcDir("src/main/graphql/github")
    }
    service("fft") {
        packageName.set("dev.simonscholz.fft")
        introspection {
            schemaFile.set(file("src/main/graphql/fft/schema.graphqls"))
        }
        srcDir("src/main/graphql/fft")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "dev.simonscholz.fft.AppKt"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
