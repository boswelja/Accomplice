package com.boswelja.accomplice

/**
 * Represents a wearable device (node) in the network.
 *
 * @param nodeId The unique wearable device ID.
 * @param displayName The display name of the wearable device.
 */
public data class WearableNode(
    val nodeId: String,
    val displayName: String
)
