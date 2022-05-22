package com.boswelja.accomplice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import java.io.InputStream
import java.io.OutputStream

class WearableManager(
    private vararg val platforms: WearablePlatform
) {
    suspend fun getNodes(): List<WearableNode> {
        return platforms.flatMap { platform ->
            platform.getNodes().map {
                it.copy(
                    nodeId = joinPlatformToId(platform, it.nodeId)
                )
            }
        }
    }

    suspend fun sendMessage(nodeId: String, message: String, payload: ByteArray? = null): Boolean {
        val (platformName, id) = separatePlatformFromId(nodeId)
        val platform = getPlatformMatching(platformName)
        return platform.sendMessage(id, message, payload)
    }

    fun receivedMessages(): Flow<ReceivedMessage> {
        return platforms
            .map { platform ->
                platform.receivedMessages().map {
                    it.copy(
                        sourceNodeId = joinPlatformToId(platform, it.sourceNodeId)
                    )
                }
            }
            .merge()
    }

    suspend fun sendData(
        nodeId: String,
        path: String,
        block: suspend OutputStream.() -> Unit
    ): Boolean {
        val (platformName, id) = separatePlatformFromId(nodeId)
        val platform = getPlatformMatching(platformName)
        return platform.sendData(id, path, block)
    }

    suspend fun receiveData(
        nodeId: String,
        path: String,
        block: suspend InputStream.() -> Unit
    ): Boolean {
        val (platformName, id) = separatePlatformFromId(nodeId)
        val platform = getPlatformMatching(platformName)
        return platform.receiveData(id, path, block)
    }

    private fun joinPlatformToId(platform: WearablePlatform, nodeId: String): String {
        return "${platform::class.qualifiedName}|$nodeId"
    }

    private fun separatePlatformFromId(nodeId: String): Pair<String, String> {
        val parts = nodeId.split("|")
        return parts[0] to parts[1]
    }

    private fun getPlatformMatching(qualifiedName: String): WearablePlatform {
        return platforms.first { it::class.qualifiedName == qualifiedName }
    }
}
