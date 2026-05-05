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
    implementation("com.mohamedrejeb.calf:calf-file-picker:0.5.3")
    implementation("com.mohamedrejeb.calf:calf-file-picker-coil:0.5.1")
    implementation("io.coil-kt.coil3:coil-compose:3.0.0-alpha08")
    testImplementation(kotlin("test"))
}

// Настройки запуска (без сложных нативных дистрибутивов)
compose.desktop {
    application {
        mainClass = "org.example.AppKt"
    }
}
//tasks.jar {
//    manifest {
//        attributes["Main-Class"] = "org.example.AppKt"
//    }
//}

//tasks.named <JavaExec>("run") {
//    standardInput = System.`in`
//}
