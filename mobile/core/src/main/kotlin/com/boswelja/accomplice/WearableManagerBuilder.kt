package com.boswelja.accomplice

/**
 * Marks a function as part of the [WearableManager] DSL
 */
@DslMarker
public annotation class WearableManagerDsl

/**
 * Builds a new [WearableManager].
 */
public fun wearableManager(init: WearableManagerBuilder.() -> Unit): WearableManager {
    return WearableManagerBuilder().apply(init).build()
}

/**
 * A DSL builder class to construct a new [WearableManager].
 */
@WearableManagerDsl
public class WearableManagerBuilder internal constructor() {
    private val platforms = mutableListOf<WearablePlatform>()

    /**
     * Adds a platform to the [WearableManager].
     */
    public fun addPlatform(platform: WearablePlatform) {
        platforms.add(platform)
    }

    internal fun build(): WearableManager {
        require(platforms.isNotEmpty())
        return WearableManager(platforms)
    }
}
