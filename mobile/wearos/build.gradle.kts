plugins {
    kotlin("android")
    id("com.android.library")
    id("io.gitlab.arturbosch.detekt")
}

android {
    namespace = "com.boswelja.accomplice.wearos"

    compileSdk = 32

    defaultConfig {
        targetSdk = 32
        minSdk = 23
    }

    kotlinOptions.freeCompilerArgs += "-Xexplicit-api=strict"
}

dependencies {
    implementation(project(":mobile:core"))
    implementation("com.google.android.gms:play-services-wearable:17.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.2")
}

detekt {
    config = files("${rootDir.absolutePath}/config/detekt/detekt-base.yml")
    basePath = rootDir.absolutePath
}
