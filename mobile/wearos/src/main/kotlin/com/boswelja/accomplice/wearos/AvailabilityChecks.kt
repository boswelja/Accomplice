package com.boswelja.accomplice.wearos

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

/**
 * Checks whether the on-device requirements are met for Wear OS support.
 *
 * @return true if we can interact with Wear OS APIs, or false otherwise.
 */
suspend fun isWearOsAvailable(context: Context): Boolean {
    return try {
        // Get an availability checker
        val apiAvailability = GoogleApiAvailability.getInstance()
        // If Play Services is not available, return false
        if (apiAvailability.isGooglePlayServicesAvailable(context) != ConnectionResult.SUCCESS) {
            return false
        }
        // Check whether a Wearable API is available, it doesn't really matter which one
        val client = Wearable.getDataClient(context)
        apiAvailability.checkApiAvailability(client).await()

        // If we make it this far, we're successful
        true
    } catch (_: ApiException) {
        // If we got ApiException somewhere, we don't have access
        false
    }
}
