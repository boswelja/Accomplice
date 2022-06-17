plugins {
    kotlin("android")
    id("com.android.library")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.dokka")
}

android {
    namespace = "com.boswelja.accomplice.core"

    compileSdk = rootProject.ext.get("compileSdk") as Int

    defaultConfig {
        targetSdk = rootProject.ext.get("targetSdk") as Int
        minSdk = rootProject.ext.get("minSdk") as Int
    }

    kotlinOptions.freeCompilerArgs += "-Xexplicit-api=strict"
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")

    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.12.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.2")
    testImplementation("app.cash.turbine:turbine:0.8.0")
}

detekt {
    config = files("${rootDir.absolutePath}/config/detekt/detekt-base.yml")
    basePath = rootDir.absolutePath
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    moduleName.set("mobile-core")
    dokkaSourceSets.named("main") {
        noAndroidSdkLink.set(false)
    }
}
