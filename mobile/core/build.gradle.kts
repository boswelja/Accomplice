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
    namespace = "com.boswelja.accomplice.core"

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
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.3")

    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.12.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.3")
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

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            pom {
                name.set("mobile-core")
                description.set("Easily connect a wearable \"accomplice\" to your app")
                url.set("https://github.com/boswelja/Accomplice/tree/main/mobile/core")
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
