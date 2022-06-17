package com.boswelja.accomplice

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

@OptIn(ExperimentalCoroutinesApi::class)
public class WearableManagerTest {

    @Test
    public fun getNodes_loadsWithSinglePlatform(): Unit = runTest {
        val testPlatform: WearablePlatform = mockk()
        val manager = wearableManager {
            addPlatform(testPlatform)
        }
        val nodes = generateNodes(10)

        coEvery { testPlatform.getNodes() } returns nodes

        assertEquals(
            nodes.size,
            manager.getNodes().size
        )

        coVerify { testPlatform.getNodes() }
    }

    @Test
    public fun getNodes_loadsWithMultiplePlatforms(): Unit = runTest {
        val platform1: WearablePlatform = mockk()
        val platform2: WearablePlatform = mockk()
        val manager = wearableManager {
            addPlatform(platform1)
            addPlatform(platform2)
        }
        val platform1Nodes = generateNodes(10)
        val platform2Nodes = generateNodes(15)

        coEvery { platform1.getNodes() } returns platform1Nodes
        coEvery { platform2.getNodes() } returns platform2Nodes

        assertEquals(
            platform1Nodes.size + platform2Nodes.size,
            manager.getNodes().size
        )

        coVerify {
            platform1.getNodes()
            platform2.getNodes()
        }
    }

    @Test
    public fun sendMessage_withValidNode_sendsToPlatform(): Unit = runTest {
        val message = "my_message_path"
        val platform1Nodes = generateNodes(10)

        val platform1: WearablePlatform = mockk()
        val manager = wearableManager {
            addPlatform(platform1)
        }
        coEvery { platform1.getNodes() } returns platform1Nodes
        coEvery { platform1.sendMessage(any(), any(), any()) } returns true

        val nodes = manager.getNodes()
        nodes.forEach { node ->
            manager.sendMessage(node.nodeId, message)
        }

        coVerify(exactly = platform1Nodes.size) { platform1.sendMessage(any(), message, null) }
    }

    @Test
    public fun sendMessage_withInvalidId_throwsException(): Unit = runTest {
        val message = "my_message_path"

        val platform1: WearablePlatform = mockk()
        val manager = wearableManager {
            addPlatform(platform1)
        }

        assertFails {
            manager.sendMessage("Some borked ID", message)
        }

        coVerify(inverse = true) { platform1.sendMessage(any(), message, null) }
    }

    @Test
    public fun receivedMessages_receivesFromAllPlatforms(): Unit = runTest {
        val platform1: WearablePlatform = mockk()
        val platform2: WearablePlatform = mockk()
        val manager = wearableManager {
            addPlatform(platform1)
            addPlatform(platform2)
        }
        val platform1Flow = MutableSharedFlow<ReceivedMessage>()
        val platform2Flow = MutableSharedFlow<ReceivedMessage>()

        every { platform1.receivedMessages() } returns platform1Flow
        every { platform2.receivedMessages() } returns platform2Flow

        manager.receivedMessages().test {
            val message = ReceivedMessage(
                "nodeId",
                "message",
                byteArrayOf(1, 2, 3)
            )

            // Send a message from platform 1
            platform1Flow.emit(message)
            awaitItem().let {
                assertEquals(
                    message.message,
                    it.message
                )
                assertEquals(
                    message.payload,
                    it.payload
                )
            }

            // Send a message from platform 2
            platform2Flow.emit(message)
            awaitItem().let {
                assertEquals(
                    message.message,
                    it.message
                )
                assertEquals(
                    message.payload,
                    it.payload
                )
            }
        }
    }

    private fun generateNodes(count: Int): List<WearableNode> {
        return (0 until count).map {
            WearableNode(
                it.toString(),
                "Node $it"
            )
        }
    }
}
