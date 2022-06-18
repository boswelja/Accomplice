// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.library") version "7.4.0-alpha05" apply false
    id("org.jetbrains.kotlin.android") version "1.7.0" apply false
    id("io.gitlab.arturbosch.detekt") version "1.20.0"
    id("org.jetbrains.dokka") version "1.6.21"
}
ext.apply {
    set("compileSdk", 33)
    set("targetSdk", 33)
    set("minSdk", 23)
}

tasks.register<Copy>("detektCollateReports") {
    // Set up task
    dependsOn(
        "mobile:core:detekt",
        "mobile:wearos:detekt",
        "wearable:wearos:detekt"
    )
    from(
        rootDir.resolve("mobile/core/build/reports/detekt/"),
        rootDir.resolve("mobile/wearos/build/reports/detekt/"),
        rootDir.resolve("wearable/wearos/build/reports/detekt/")
    )
    include("detekt.sarif")

    // Delete any existing contents
    buildDir.resolve("reports/detekt/").deleteRecursively()

    // Set up copy
    destinationDir = buildDir.resolve("reports/detekt/")
    rename {
        val totalCount = destinationDir.list()?.count()
        "$totalCount-$it"
    }
}