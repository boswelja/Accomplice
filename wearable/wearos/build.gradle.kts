plugins {
    kotlin("android")
    id("com.android.library")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.dokka")
    id("signing")
    id("maven-publish")
}

group = "io.github.boswelja.accomplice"

android {
    namespace = "com.boswelja.accomplice.wearos"

    compileSdk = rootProject.ext.get("compileSdk") as Int

    defaultConfig {
        targetSdk = rootProject.ext.get("targetSdk") as Int
        minSdk = rootProject.ext.get("minSdk") as Int
    }

    kotlinOptions.freeCompilerArgs += "-Xexplicit-api=strict"

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation("com.google.android.gms:play-services-wearable:17.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.3")
}

detekt {
    config = files("${rootDir.absolutePath}/config/detekt/detekt-base.yml")
    basePath = rootDir.absolutePath
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    moduleName.set("wearable-wearos")
    dokkaSourceSets.named("main") {
        noAndroidSdkLink.set(false)
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            pom {
                name.set("wearable-wearos")
                description.set("Easily connect a wearable \"accomplice\" to your app")
                url.set("https://github.com/boswelja/Accomplice/tree/main/wearable/wearos")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://github.com/boswelja/Accomplice/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("boswelja")
                        name.set("Jack Boswell")
                        email.set("boswelja@outlook.com")
                        url.set("https://github.com/boswelja")
                    }
                }
                scm {
                    connection.set("scm:git:github.com/boswelja/Accomplice.git")
                    developerConnection.set("scm:git:ssh://github.com/boswelja/Accomplice.git")
                    url.set("https://github.com/boswelja/Accomplice")
                }
            }

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
