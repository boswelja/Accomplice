plugins {
    kotlin("android")
    id("com.android.library")
    id("io.gitlab.arturbosch.detekt")
}

android {
    namespace = "com.boswelja.accomplice.core"

    compileSdk = 32

    defaultConfig {
        targetSdk = 32
        minSdk = 23
    }
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")

    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.12.4")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")
}

detekt {
    config = files("${rootDir.absolutePath}/config/detekt/detekt-base.yml")
    basePath = rootDir.absolutePath
}
