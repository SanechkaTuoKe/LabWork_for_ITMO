plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.compose") version "2.0.21"
    id("org.jetbrains.compose") version "1.7.0"
}

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)

    testImplementation(kotlin("test"))
}

// Настройки запуска (без сложных нативных дистрибутивов)
compose.desktop {
    application {
        mainClass = "org.example.MainKt"
    }
}