package com.boswelja.accomplice.wearos

import android.content.Context
import com.boswelja.accomplice.ConnectionState
import com.boswelja.accomplice.ReceivedMessage
import com.boswelja.accomplice.WearableNode
import com.boswelja.accomplice.WearablePlatform
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.MessageClient.OnMessageReceivedListener
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.InputStream
import java.io.OutputStream

internal class WearOsPlatformImpl(
    private val context: Context,
    private val applicationCapability: String
) : WearablePlatform {

    private val messageClient by lazy { Wearable.getMessageClient(context) }
    private val channelClient by lazy { Wearable.getChannelClient(context) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(context) }

    override suspend fun getNodes(): List<WearableNode> {
        val nodes = capabilityClient.getCapability(
            applicationCapability,
            CapabilityClient.FILTER_ALL
        ).await()

        return nodes.nodes.map { node ->
            WearableNode(node.id, node.displayName)
        }
    }

    override suspend fun sendMessage(
        nodeId: String,
        message: String,
        payload: ByteArray?
    ): Boolean {
        messageClient.sendMessage(nodeId, message, payload).await()
        return true
    }

    override fun receivedMessages(): Flow<ReceivedMessage> {
        return callbackFlow {
            val callback = OnMessageReceivedListener { messageEvent ->
                trySend(
                    ReceivedMessage(
                        sourceNodeId = messageEvent.sourceNodeId,
                        message = messageEvent.path,
                        payload = messageEvent.data
                    )
                )
            }
            messageClient.addListener(callback)

            awaitClose {
                messageClient.removeListener(callback)
            }
        }
    }

    override suspend fun sendData(
        nodeId: String,
        path: String,
        block: suspend OutputStream.() -> Unit
    ) {
        val channel = channelClient.openChannel(nodeId, path).await()
        channelClient.getOutputStream(channel).await().use {
            it.block()
        }
        channelClient.close(channel).await()
    }

    override suspend fun receiveData(
        nodeId: String,
        path: String,
        block: suspend InputStream.() -> Unit
    ) {
        val channel = channelClient.openChannel(nodeId, path).await()
        channelClient.getInputStream(channel).await().use {
            it.block()
        }
        channelClient.close(channel).await()
    }

    override suspend fun getConnectionState(nodeId: String): ConnectionState {
        val reachableNodes = capabilityClient
            .getCapability(applicationCapability, CapabilityClient.FILTER_REACHABLE)
            .await()
            .nodes
        return if (reachableNodes.any { it.id == nodeId }) {
            ConnectionState.CONNECTED
        } else {
            ConnectionState.DISCONNECTED
        }
    }
}
