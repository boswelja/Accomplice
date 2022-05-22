package com.boswelja.accomplice

import kotlinx.coroutines.flow.Flow
import java.io.InputStream
import java.io.OutputStream

interface WearablePlatform {

    suspend fun getNodes(): List<WearableNode>

    suspend fun sendMessage(nodeId: String, message: String, payload: ByteArray?): Boolean

    fun receivedMessages(): Flow<ReceivedMessage>

    suspend fun sendData(
        nodeId: String,
        path: String,
        block: suspend OutputStream.() -> Unit
    ): Boolean

    suspend fun receiveData(
        nodeId: String,
        path: String,
        block: suspend InputStream.() -> Unit
    ): Boolean
}
