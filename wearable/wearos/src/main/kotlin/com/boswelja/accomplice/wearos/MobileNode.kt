package com.boswelja.accomplice.wearos

/**
 * Represents a mobile device connected to this wearable.
 *
 * @param nodeId The unique mobile device ID.
 * @param displayName The display name of the mobile device.
 */
data class MobileNode(
    val nodeId: String,
    val displayName: String
)
