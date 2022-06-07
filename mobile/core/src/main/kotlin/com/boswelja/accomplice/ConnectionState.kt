package com.boswelja.accomplice

/**
 * Possible connection states for [WearableNode]s in the network.
 */
enum class ConnectionState {

    /**
     * Indicates the node is connected and reachable.
     */
    CONNECTED,

    /**
     * Indicates the node is not connected, and cannot be reached.
     */
    DISCONNECTED
}
