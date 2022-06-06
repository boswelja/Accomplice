package com.boswelja.accomplice.wearos

import android.content.Context
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.InputStream
import java.io.OutputStream

class WearableManager(
    private val context: Context,
    private val applicationCapability: String
) {

    private val messageClient by lazy { Wearable.getMessageClient(context) }
    private val channelClient by lazy { Wearable.getChannelClient(context) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(context) }

    suspend fun sendMessage(
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
        } catch (e: CancellationException) {
            // Coroutine was cancelled
            false
        }
    }

    fun receivedMessages(): Flow<ReceivedMessage> {
        return callbackFlow {
            val target = connectedDevice()
            val callback = MessageClient.OnMessageReceivedListener { messageEvent ->
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

    suspend fun sendData(
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
        } catch (e: CancellationException) {
            // Coroutine was cancelled
            false
        }
    }

    suspend fun receiveData(
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
        } catch (e: CancellationException) {
            // Coroutine was cancelled
            false
        }
    }

    suspend fun connectedDevice(): MobileNode {
        val nodes = capabilityClient
            .getCapability(applicationCapability, CapabilityClient.FILTER_ALL)
            .await()
            .nodes
        val node = nodes.firstOrNull { it.isNearby } ?: nodes.first()
        return MobileNode(node.id, node.displayName)
    }
}
