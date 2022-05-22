package com.boswelja.accomplice

/**
 * Marks a function as part of the [WearableManager] DSL
 */
@DslMarker
annotation class WearableManagerDsl

/**
 * Builds a new [WearableManager].
 */
fun wearableManager(init: WearableManagerBuilder.() -> Unit): WearableManager {
    return WearableManagerBuilder().apply(init).build()
}

/**
 * A DSL builder class to construct a new [WearableManager].
 */
@WearableManagerDsl
class WearableManagerBuilder internal constructor() {
    private val platforms = mutableListOf<WearablePlatform>()

    /**
     * Adds a platform to the [WearableManager].
     */
    fun addPlatform(platform: WearablePlatform) {
        platforms.add(platform)
    }

    internal fun build(): WearableManager {
        require(platforms.isNotEmpty())
        return WearableManager(platforms)
    }
}
