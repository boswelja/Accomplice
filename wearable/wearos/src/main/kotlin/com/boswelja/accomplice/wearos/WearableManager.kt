package com.boswelja.accomplice.wearos

import android.content.Context
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Manages connections with the mobile device that's connected to the wearable.
 *
 * @param context [Context].
 * @param applicationCapability A unique capability used to identify the mobile device in the node
 * map.
 */
public class WearableManager(
    private val context: Context,
    private val applicationCapability: String
) {

    private val messageClient by lazy { Wearable.getMessageClient(context) }
    private val channelClient by lazy { Wearable.getChannelClient(context) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(context) }

    /**
     * Sends a message to the connected mobile device. Messages have no form of synchronization, and
     * should not be used if reliability is a concern.
     *
     * @param message A unique identifier for this message.
     * @param payload An optional payload for the message.
     *
     * @return true if the message was sent successfully, false otherwise. Note a successful send
     * does *not* guarantee delivery.
     */
    public suspend fun sendMessage(
        message: String,
        payload: ByteArray?
    ): Boolean {
        return try {
            val target = connectedDevice()
            messageClient.sendMessage(target.nodeId, message, payload).await()
            true
        } catch (e: ApiException) {
            // Message sending failed
            false
        }
    }

    /**
     * Flows all [ReceivedMessage]s from the connected mobile device.
     */
    public fun receivedMessages(): Flow<ReceivedMessage> {
        return callbackFlow {
            val target = connectedDevice()
            val callback = MessageClient.OnMessageReceivedListener { messageEvent ->
                // Filter out unrelated messages, just in case
                if (messageEvent.sourceNodeId == target.nodeId) {
                    trySend(
                        ReceivedMessage(
                            message = messageEvent.path,
                            payload = messageEvent.data
                        )
                    )
                }
            }
            messageClient.addListener(callback)

            awaitClose {
                messageClient.removeListener(callback)
            }
        }
    }

    /**
     * Opens an [OutputStream] to send data to the connected mobile device.
     *
     * @param path A unique path for the data to be sent on.
     * @param block A block of code scoped to the [OutputStream]. The stream is closed automatically
     * once execution has completed. Note exceptions in this block will be caught automatically.
     *
     * @return true if the data was sent successfully, false otherwise.
     */
    public suspend fun sendData(
        path: String,
        block: suspend OutputStream.() -> Unit
    ): Boolean {
        return try {
            val target = connectedDevice()
            val channel = channelClient.openChannel(target.nodeId, path).await() ?: return false
            channelClient.getOutputStream(channel).await().use {
                it.block()
            }
            channelClient.close(channel).await()
            true
        } catch (e: IOException) {
            // IOException writing data
            false
        }
    }

    /**
     * Opens an [InputStream] to receive data from the connected mobile device.
     *
     * @param path A unique path to listen for incoming data on.
     * @param block A block of code scoped to the [InputStream]. The stream is closed automatically
     * once execution has completed. Note exceptions in this block will be caught automatically.
     *
     * @return true if the data was received successfully, false otherwise.
     */
    public suspend fun receiveData(
        path: String,
        block: suspend InputStream.() -> Unit
    ): Boolean {
        return try {
            val target = connectedDevice()
            val channel = channelClient.openChannel(target.nodeId, path).await() ?: return false
            channelClient.getInputStream(channel).await().use {
                it.block()
            }
            channelClient.close(channel).await()
            true
        } catch (e: IOException) {
            // IOException reading data
            false
        }
    }

    /**
     * Gets the [ConnectionState] for the mobile device paired to this wearable.
     */
    public suspend fun getConnectionState(): ConnectionState {
        val targetDevice = connectedDevice()
        val reachableNodes = capabilityClient
            .getCapability(applicationCapability, CapabilityClient.FILTER_REACHABLE)
            .await()
            .nodes

        return if (reachableNodes.any { it.id == targetDevice.nodeId }) {
            ConnectionState.CONNECTED
        } else {
            ConnectionState.DISCONNECTED
        }
    }

    /**
     * Retrieves a [MobileNode] representing the mobile device paired to this wearable.
     */
    public suspend fun connectedDevice(): MobileNode {
        val nodes = capabilityClient
            .getCapability(applicationCapability, CapabilityClient.FILTER_ALL)
            .await()
            .nodes
        val node = nodes.firstOrNull { it.isNearby } ?: nodes.first()
        return MobileNode(node.id, node.displayName)
    }
}
