package com.boswelja.accomplice.wearos

import android.content.Context
import com.boswelja.accomplice.WearableManagerBuilder
import com.boswelja.accomplice.WearableManagerDsl

@WearableManagerDsl
fun WearableManagerBuilder.registerWearOSPlatform(context: Context, applicationCapability: String) {
    addPlatform(WearOSPlatformImpl(context, applicationCapability))
}
