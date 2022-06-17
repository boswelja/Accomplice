plugins {
    kotlin("android")
    id("com.android.library")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.dokka")
}

android {
    namespace = "com.boswelja.accomplice.wearos"

    compileSdk = rootProject.ext.get("compileSdk") as Int

    defaultConfig {
        targetSdk = rootProject.ext.get("targetSdk") as Int
        minSdk = rootProject.ext.get("minSdk") as Int
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

tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    moduleName.set("mobile-wearos")
    dokkaSourceSets.named("main") {
        noAndroidSdkLink.set(false)
    }
}
