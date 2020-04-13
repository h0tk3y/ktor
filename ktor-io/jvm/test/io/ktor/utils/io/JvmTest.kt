package io.ktor.utils.io

import io.ktor.utils.io.core.*
import org.junit.*
import org.junit.rules.*
import java.util.concurrent.*
import kotlin.test.*
import kotlin.test.Test
import java.nio.*

open class JvmByteChannelSmokeTest : ByteChannelSmokeTest() {

    @get:Rule
    val timeout = Timeout(10, TimeUnit.SECONDS)

    @Test
    fun testReadAndWriteFullyByteBuffer() {
        runTest {
            val bytes = byteArrayOf(1, 2, 3, 4, 5)
            val dst = ByteArray(5)

            val ch: ByteChannel = channel

            ch.writeFully(ByteBuffer.wrap(bytes))
            ch.flush()
            assertEquals(5, ch.availableForRead)
            ch.readFully(ByteBuffer.wrap(dst))
            assertTrue { dst.contentEquals(bytes) }

            ch.writeFully(ByteBuffer.wrap(bytes))
            ch.flush()

            val dst2 = ByteArray(4)
            ch.readFully(ByteBuffer.wrap(dst2))

            assertEquals(1, ch.availableForRead)
            assertEquals(5, ch.readByte())

            ch.close()

            try {
                ch.readFully(ByteBuffer.wrap(dst))
                fail("")
            } catch (expected: EOFException) {
            }
        }
    }

    @Test
    fun testReadAndWritePartiallyByteBuffer() {
        runTest {
            val bytes = byteArrayOf(1, 2, 3, 4, 5)

            assertEquals(5, channel.writeAvailable(ByteBuffer.wrap(bytes)))
            channel.flush()
            assertEquals(5, channel.readAvailable(ByteBuffer.allocate(100)))

            repeat(Size / bytes.size) {
                assertNotEquals(0, channel.writeAvailable(ByteBuffer.wrap(bytes)))
                channel.flush()
            }

            channel.readAvailable(ByteArray(channel.availableForRead - 1))
            assertEquals(1, channel.readAvailable(ByteBuffer.allocate(100)))

            channel.close()
        }
    }

    override fun ByteChannel(autoFlush: Boolean): ByteChannel {
        return ByteChannelSequentialJVM(IoBuffer.Empty, autoFlush)
    }
}
