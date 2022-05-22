plugins {
    kotlin("android")
    id("com.android.library")
}

android {
    namespace = "com.boswelja.accomplice"

    compileSdk = 32

    defaultConfig {
        targetSdk = 32
        minSdk = 23
    }
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
}
