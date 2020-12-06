plugins {
    kotlin("jvm") version "1.4.10"
    application
}

group = "me.predovic"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("me.tongfei:progressbar:0.9.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

application {
    mainClass.set("MainKt")
}
