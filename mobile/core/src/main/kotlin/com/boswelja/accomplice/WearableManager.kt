package com.boswelja.accomplice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import java.io.InputStream
import java.io.OutputStream

/**
 * A class for managing and using [WearablePlatform]s.
 */
class WearableManager(
    private val platforms: List<WearablePlatform>
) {

    /**
     * Retrieves a list of all [WearableNode]s found for all platforms.
     */
    suspend fun getNodes(): List<WearableNode> {
        return platforms.flatMap { platform ->
            platform.getNodes().map {
                it.copy(
                    nodeId = joinPlatformToId(platform, it.nodeId)
                )
            }
        }
    }

    /**
     * Sends a message to the [WearableNode] with the given ID.
     *
     * @param nodeId The [WearableNode.nodeId] of the target node.
     * @param message The message to send.
     * @param payload An optional payload to send with the message.
     *
     * @return true if the message was sent successfully, false otherwise. Note a successful send
     * does *not* guarantee successful delivery.
     */
    suspend fun sendMessage(nodeId: String, message: String, payload: ByteArray? = null): Boolean {
        val (platformName, id) = separatePlatformFromId(nodeId)
        val platform = getPlatformMatching(platformName)
        return platform.sendMessage(id, message, payload)
    }

    /**
     * Flows all [ReceivedMessage]s from all platforms.
     */
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

    /**
     * Opens an [OutputStream] to send data to a specific node.
     *
     * @param nodeId The [WearableNode.nodeId] to send data to.
     * @param path A unique path for the data to be sent on.
     * @param block A block of code scoped to the [OutputStream]. The stream is closed automatically
     * once execution has completed. Note exceptions in this block will be caught automatically.
     *
     * @return true if the data was sent successfully, false otherwise.
     */
    suspend fun sendData(
        nodeId: String,
        path: String,
        block: suspend OutputStream.() -> Unit
    ): Boolean {
        val (platformName, id) = separatePlatformFromId(nodeId)
        val platform = getPlatformMatching(platformName)
        return platform.sendData(id, path, block)
    }

    /**
     * Opens an [InputStream] to receive data from a specific node.
     *
     * @param nodeId The [WearableNode.nodeId] to receive data from.
     * @param path A unique path to listen for incoming data on.
     * @param block A block of code scoped to the [InputStream]. The stream is closed automatically
     * once execution has completed. Note exceptions in this block will be caught automatically.
     *
     * @return true if the data was received successfully, false otherwise.
     */
    suspend fun receiveData(
        nodeId: String,
        path: String,
        block: suspend InputStream.() -> Unit
    ): Boolean {
        val (platformName, id) = separatePlatformFromId(nodeId)
        val platform = getPlatformMatching(platformName)
        return platform.receiveData(id, path, block)
    }

    /**
     * Joins a unique identifier for the given platform with the given node ID. This is necessary
     * for [WearableManager] to be able to correctly identify the platform of origin for this node.
     */
    private fun joinPlatformToId(platform: WearablePlatform, nodeId: String): String {
        return "${platform::class.qualifiedName}|$nodeId"
    }

    /**
     * Separates the unique identifier from node ID for the given joined ID. This is necessary
     * for [WearableManager] to be able to correctly identify the platform of origin for this node.
     */
    private fun separatePlatformFromId(nodeId: String): Pair<String, String> {
        val parts = nodeId.split("|")
        return parts[0] to parts[1]
    }

    /**
     * Returns the first platform whose qualified name matches [qualifiedName]
     */
    private fun getPlatformMatching(qualifiedName: String): WearablePlatform {
        return platforms.first { it::class.qualifiedName == qualifiedName }
    }
}
