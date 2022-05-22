package com.boswelja.accomplice

/**
 * A message received from a [WearableNode].
 *
 * @param sourceNodeId See [WearableNode.nodeId].
 * @param message The message that was received.
 * @param payload An optional payload.
 */
data class ReceivedMessage(
    val sourceNodeId: String,
    val message: String,
    val payload: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReceivedMessage

        if (sourceNodeId != other.sourceNodeId) return false
        if (message != other.message) return false
        if (payload != null) {
            if (other.payload == null) return false
            if (!payload.contentEquals(other.payload)) return false
        } else if (other.payload != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sourceNodeId.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + (payload?.contentHashCode() ?: 0)
        return result
    }
}
