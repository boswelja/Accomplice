package com.boswelja.accomplice.wearos

/**
 * Possible connection states for [MobileNode]s in the network.
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
