package com.boswelja.accomplice.wearos

import android.content.Context
import com.boswelja.accomplice.WearableManagerBuilder
import com.boswelja.accomplice.WearableManagerDsl

/**
 * Adds support for the WearOS platform to this WearableManager
 */
@WearableManagerDsl
fun WearableManagerBuilder.registerWearOSPlatform(context: Context, applicationCapability: String) {
    addPlatform(WearOsPlatformImpl(context, applicationCapability))
}
