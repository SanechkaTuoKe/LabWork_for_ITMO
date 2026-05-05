plugins {
    // Kotlin JVM plugin
    kotlin("jvm") version "2.2.21"

    // Application plugin для запуска main()
    application
}

repositories {
    // Репозиторий для зависимостей
    mavenCentral()
}

dependencies {
    // Kotlin Test
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Пример внешней библиотеки
    implementation("com.google.guava:guava:32.1.2-jre")
}

// Настройка Java Toolchain
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// Настройка точки входа для приложения
application {
    // Имя класса для функции main()
    mainClass.set("org.example.AppKt")
}

// Используем JUnit Platform для тестов
tasks.named<Test>("test") {
    useJUnitPlatform()
}

// Включаем стандартный ввод для gradle run
tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}