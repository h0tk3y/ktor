package io.ktor.utils.io

import io.ktor.utils.io.bits.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.core.internal.*
import writePacket
import writeStringUtf8
import kotlin.test.*

open class ByteBufferChannelScenarioTest : ByteChannelTestBase(true) {

    @Test
    fun testReadBeforeAvailable() = runTest {
        expect(1)

        launch {
            expect(3)

            val readCount = channel.readAvailable(ByteArray(1024)) // should suspend

            expect(5)
            assertEquals(4, readCount)

            expect(6)
        }

        expect(2)
        yield()

        expect(4)
        channel.writeInt(0xff) // should resume

        yield()

        finish(7)
    }

    @Test
    fun testReadBeforeAvailable2() = runTest {
        expect(1)

        launch {
            expect(3)

            channel.readFully(ByteArray(4)) // should suspend

            expect(5)

            expect(6)
        }

        expect(2)
        yield()

        expect(4)
        channel.writeInt(0xff) // should resume

        yield()

        finish(7)
    }

    @Test
    fun testReadAfterAvailable() = runTest {
        expect(1)

        channel.writeInt(0xff) // should resume

        launch {
            expect(3)

            val rc = channel.readAvailable(ByteArray(10)) // should NOT suspend

            expect(4)
            assertEquals(4, rc)

            expect(5)
        }

        expect(2)
        yield()

        finish(6)
    }

    @Test
    fun testReadAfterAvailable2() = runTest {
        expect(1)

        channel.writeInt(0xff) // should resume

        launch {
            expect(3)
            channel.readFully(ByteArray(4)) // should NOT suspend

            expect(4)
            expect(5)
        }

        expect(2)
        yield()

        finish(6)
    }

    @Test
    fun testReadToEmpty() = runTest {
        expect(1)

        val rc = channel.readAvailable(EmptyByteArray)

        expect(2)

        assertEquals(0, rc)

        finish(3)
    }

    @Test
    fun testReadToEmptyFromFailedChannel() = runTest {
        expect(1)

        channel.close(ExpectedException())

        try {
            channel.readAvailable(EmptyByteArray)
            fail("Should throw exception")
        } catch (expected: ExpectedException) {
        }

        finish(2)
    }

    @Test
    fun testReadToEmptyFromClosedChannel() = runTest {
        expect(1)

        channel.close()

        val rc = channel.readAvailable(EmptyByteArray)

        expect(2)

        assertEquals(-1, rc)

        finish(3)
    }

    @Test
    fun testReadFullyToEmptyFromClosedChannel() = runTest {
        expect(1)

        channel.close()

        channel.readFully(EmptyByteArray)

        finish(2)
    }

    @Test
    fun testReadFullyFromClosedChannel() = runTest() {
        expect(1)

        channel.close()
        try {
            channel.readFully(EmptyByteArray)
            fail("Should throw exception")
        } catch (expected: Throwable) {
        }

        finish(2)
    }

    @Test
    fun testReadFullyToEmptyFromFailedChannel() = runTest {
        expect(1)

        channel.close(ExpectedException())

        try {
            channel.readFully(EmptyByteArray)
            fail("Should throw exception")
        } catch (expected: ExpectedException) {
        }

        finish(2)
    }

    @Test
    fun testWritePacket() = runTest {
        launch {
            expect(1)

            channel.writePacket {
                writeLong(0x1234567812345678L)
            }

            expect(2)
        }

        yield()
        expect(3)

        assertEquals(0x1234567812345678L, channel.readLong())
        assertEquals(0, channel.availableForRead)

        finish(4)
    }

    @Test
    fun testWriteBlockSuspend() = runTest {
        launch {
            expect(1)

            channel.writeFully(ByteArray(4088))

            expect(2)

            channel.writePacket(8) {
                writeLong(0x1234567812345678L)
            }

            expect(4)
        }

        yield()
        expect(3)

        channel.readFully(ByteArray(9))
        yield()
        expect(5)

        channel.readFully(ByteArray(4088 - 9))

        expect(6)

        assertEquals(0x1234567812345678L, channel.readLong())
        assertEquals(0, channel.availableForRead)

        finish(7)
    }

    @Test
    fun testNewWriteBlock() = runTest {
        launch {
            assertEquals(0x11223344, channel.readInt())
        }

        channel.write (4) { freeSpace, startOffset, endExclusive ->
            if (endExclusive - startOffset < 4) {
                fail("Not enough free space for writing 4 bytes: ${endExclusive - startOffset}")
            }

            freeSpace.storeIntAt(startOffset, 0x11223344)

            4
        }
        channel.close()
    }

    @Test
    fun testReadBlock() = runTest {
        channel.writeLong(0x1234567812345678L)
        channel.flush()

        val packet = channel.readPacket(8)
        assertEquals(0x1234567812345678L, packet.readLong())

        finish(1)
    }

    @Test
    fun testReadBlockSuspend() = runTest {
        channel.writeByte(0x12)

        launch {
            expect(1)
            var readCount = 0
            while (readCount < 8) {
                channel.read { memory, start, end ->
                    val count = end - start
                    readCount += count
                    count
                }
            }

            expect(3)
        }

        yield()
        expect(2)

        channel.writeLong(0x3456781234567800L)
        yield()

        expect(4)
        channel.readByte()
        assertEquals(0, channel.availableForRead)

        finish(5)
    }

    @Test
    fun testReadMemoryBlock() = runTest {
        launch {
            channel.writeInt(0x11223344)
            channel.close()
        }

        channel.read(4) { source, start, endExclusive ->
            if (endExclusive - start < 4) {
                fail("It should be 4 bytes available, got ${endExclusive - start}")
            }

            assertEquals(0x11223344, source.loadIntAt(start))
            4
        }

        assertEquals(0, channel.availableForRead)
    }

    @Test
    fun testWriteByteSuspend() = runTest {
        launch {
            expect(1)
            channel.writeByte(1)
            channel.writeFully(ByteArray(channel.availableForWrite))
            expect(2)
            channel.writeByte(1)
            expect(5)
            channel.close()
        }

        yield()
        expect(3)
        yield()
        expect(4)
        yield()

        channel.readByte()
        yield()

        channel.readRemaining()
        finish(6)
    }

    @Test
    fun testWriteShortSuspend() = runTest {
        launch {
            expect(1)
            channel.writeByte(1)
            channel.writeFully(ByteArray(channel.availableForWrite))
            expect(2)
            channel.writeShort(1)
            expect(5)
            channel.close()
        }

        yield()
        expect(3)
        yield()
        expect(4)
        yield()

        channel.readShort()
        yield()

        channel.readRemaining()
        finish(6)
    }

    @Test
    fun testWriteIntSuspend() = runTest {
        launch {
            expect(1)
            channel.writeByte(1)
            channel.writeFully(ByteArray(channel.availableForWrite))
            expect(2)
            channel.writeInt(1)
            expect(5)
            channel.close()
        }

        yield()
        expect(3)
        yield()
        expect(4)
        yield()

        channel.readInt()
        yield()

        channel.readRemaining()
        finish(6)
    }

    @Test
    fun testWriteIntThenRead() = runTest {
        val size = 4096 - 8 - 3

        expect(1)
        val buffer = buildPacket {
            repeat(size) {
                writeByte(1)
            }
        }

        channel.writePacket(buffer)
        expect(2)

        launch {
            expect(4)
            debug(channel.readPacket(size))
            expect(5)
        }

        // coroutine is pending
        expect(3)
        channel.writeInt(0x11223344)
        yield()
        expect(6)

        assertEquals(0x11223344, channel.readInt())

        finish(7)
    }

    @Test
    fun testWriteByteByByte() = runTest {
        channel.writeByte(1)
        channel.flush()
        channel.writeByte(2)
        channel.flush()

        assertEquals(2, channel.availableForRead)
        channel.discardExact(2)
    }

    @Test
    fun testWriteByteByByteLong() = runTest {
        launch {
            repeat(16384) {
                channel.writeByte(it and 0x0f)
                channel.flush()
            }
            channel.close()
        }

        yield()
        channel.discardExact(16384)
    }

    private fun debug(p: ByteReadPacket) {
        p.release()
    }

    @Test
    fun testWriteLongSuspend() = runTest {
        launch {
            expect(1)
            channel.writeByte(1)
            channel.writeFully(ByteArray(channel.availableForWrite))
            expect(2)
            channel.writeLong(1)
            expect(5)
            channel.close()
        }

        yield()
        expect(3)
        yield()
        expect(4)
        yield()

        channel.readLong()
        yield()

        channel.readRemaining()
        finish(6)
    }

    @Test
    fun testDiscardExisting() = runTest {
        launch {
            expect(1)
            channel.writeInt(1)
            channel.writeInt(2)
            expect(2)
        }

        yield()
        expect(3)

        assertEquals(4, channel.discard(4))
        assertEquals(2, channel.readInt())

        finish(4)
    }

    @Test
    fun testDiscardPartiallyExisting() = runTest {
        channel.writeInt(1)

        launch {
            expect(1)
            assertEquals(8, channel.discard(8))
            expect(3)
        }

        yield()
        expect(2)

        channel.writeInt(2)
        yield()

        expect(4)
        assertEquals(0, channel.availableForRead)
        finish(5)
    }

    @Test
    fun testDiscardPartiallyExisting2() = runTest {
        launch {
            expect(1)
            assertEquals(8, channel.discard(8))
            expect(4)
        }

        yield()

        expect(2)
        channel.writeInt(1)
        yield()
        expect(3)
        assertEquals(0, channel.availableForRead)

        channel.writeInt(2)
        yield()
        expect(5)
        assertEquals(0, channel.availableForRead)
        finish(6)
    }

    @Test
    fun testDiscardClose() = runTest {
        launch {
            expect(1)
            assertEquals(8, channel.discard())
            expect(4)
        }

        yield()

        expect(2)
        channel.writeInt(1)
        yield()
        channel.writeInt(2)
        yield()

        expect(3)
        channel.close()
        yield()

        finish(5)
    }

    @Test
    fun testUnicode() = runTest {
        channel.writeStringUtf8("test - \u0422")
        channel.close()

        assertEquals("test - \u0422", channel.readUTF8Line())
    }

    class ExpectedException : Exception()
}
