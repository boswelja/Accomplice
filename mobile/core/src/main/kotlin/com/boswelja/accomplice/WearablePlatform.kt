package com.boswelja.accomplice

import kotlinx.coroutines.flow.Flow
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * An interface for connecting to a single Wearable platform (think Wear OS, Fitbit etc).
 */
public interface WearablePlatform {

    /**
     * Retrieves a list of all [WearableNode]s from the platform.
     */
    public suspend fun getNodes(): List<WearableNode>

    /**
     * Sends a message to the node with the given ID. Messages have no form of synchronization, and
     * should not be used if reliability is a concern.
     *
     * @param nodeId The [WearableNode.nodeId] of the target node.
     * @param message A unique identifier for this message.
     * @param payload An optional payload for the message.
     *
     * @return true if the message was sent successfully, false otherwise. Note a successful send
     * does *not* guarantee delivery.
     */
    public suspend fun sendMessage(nodeId: String, message: String, payload: ByteArray?): Boolean

    /**
     * Flows all [ReceivedMessage]s from this platform.
     */
    public fun receivedMessages(): Flow<ReceivedMessage>

    /**
     * Opens an [OutputStream] to send data to a specific node.
     *
     * @param nodeId The [WearableNode.nodeId] to send data to.
     * @param path A unique path for the data to be sent on.
     * @param block A block of code scoped to the [OutputStream]. The stream is closed automatically
     * once execution has completed. Note exceptions in this block will be caught automatically.
     */
    @Throws(IOException::class)
    public suspend fun sendData(
        nodeId: String,
        path: String,
        block: suspend OutputStream.() -> Unit
    )

    /**
     * Opens an [InputStream] to receive data from a specific node.
     *
     * @param nodeId The [WearableNode.nodeId] to receive data from.
     * @param path A unique path to listen for incoming data on.
     * @param block A block of code scoped to the [InputStream]. The stream is closed automatically
     * once execution has completed. Note exceptions in this block will be caught automatically.
     */
    @Throws(IOException::class)
    public suspend fun receiveData(
        nodeId: String,
        path: String,
        block: suspend InputStream.() -> Unit
    )

    /**
     * Gets the [ConnectionState] for the node with the specified ID.
     */
    public suspend fun getConnectionState(nodeId: String): ConnectionState
}
