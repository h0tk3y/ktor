package io.ktor.utils.io

import io.ktor.utils.io.core.*
import io.ktor.utils.io.core.EOFException
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.*
import kotlin.math.*
import kotlin.test.*

open class ByteChannelSmokeTest : ByteChannelTestBase() {

    @Test
    fun simpleSmokeTest() {
        val bc = ByteChannel(false)
        bc.close()
    }

    @Test
    open fun testWriteInt() = runTest {
        val bc = ByteChannel(false)
        bc.writeInt(777)
        bc.flush()
        assertEquals(777, bc.readInt())
    }

    @Test
    fun testWriteLong() = runTest {
        val bc = ByteChannel(false)
        bc.writeLong(777)
        bc.flush()
        assertEquals(777L, bc.readLong())
    }

    @Test
    fun testReadLineClosed() = runTest {
        val bc = ByteChannel(true)
        bc.writeStringUtf8("Test")
        bc.close()

        val s = buildString {
            bc.readUTF8LineTo(this)
        }

        assertEquals("Test", s)
    }

    @Test
    fun testReadLine() = runTest {
        val bc = ByteChannel(true)
        bc.writeStringUtf8("Test\n")
        bc.flush()

        val s = buildString {
            bc.readUTF8LineTo(this)
        }

        assertEquals("Test", s)
    }

    @Test
    fun testReadLineFromExceptionallyClosedChannel() = runTest {
        val bc = ByteChannel(true)
        bc.writeStringUtf8("Test\n")
        bc.flush()
        bc.close(IllegalStateException("Something goes wrong"))

        val exception = assertFailsWith<IllegalStateException> {
            bc.readUTF8LineTo(StringBuilder(), 10)
        }

        assertEquals("Something goes wrong", exception.message)
    }

    @Test
    fun testBoolean() {
        runTest {
            channel.writeBoolean(true)
            channel.flush()
            assertEquals(true, channel.readBoolean())

            channel.writeBoolean(false)
            channel.flush()
            assertEquals(false, channel.readBoolean())
        }
    }

    @Test
    fun testByte() {
        runTest {
            assertEquals(0, channel.availableForRead)
            channel.writeByte(-1)
            channel.flush()
            assertEquals(1, channel.availableForRead)
            assertEquals(-1, channel.readByte())
            assertEquals(0, channel.availableForRead)
        }
    }

    @Test
    fun testShortB() {
        runTest {
            assertEquals(0, channel.availableForRead)
            channel.writeShort(-1)
            assertEquals(0, channel.availableForRead)
            channel.flush()
            assertEquals(2, channel.availableForRead)
            assertEquals(-1, channel.readShort())
            assertEquals(0, channel.availableForRead)
        }
    }

    @Test
    fun testShortL() {
        runTest {
            assertEquals(0, channel.availableForRead)
            channel.writeShortLittleEndian(-1)
            assertEquals(0, channel.availableForRead)
            channel.flush()
            assertEquals(2, channel.availableForRead)
            assertEquals(-1, channel.readShortLittleEndian())
            assertEquals(0, channel.availableForRead)
        }
    }

    @Test
    fun testShortEdge() {
        runTest {
            channel.writeByte(1)

            for (i in 2 until Size step 2) {
                channel.writeShort(0x00ee)
            }

            channel.flush()

            channel.readByte()
            channel.writeShort(0x1234)

            channel.flush()

            while (channel.availableForRead > 2) {
                channel.readShort()
            }

            assertEquals(0x1234, channel.readShort())
        }
    }

    @Test
    fun testIntB() {
        runTest {
            assertEquals(0, channel.availableForRead)
            channel.writeInt(-1)
            channel.flush()
            assertEquals(4, channel.availableForRead)
            assertEquals(-1, channel.readInt())
            assertEquals(0, channel.availableForRead)
        }
    }

    @Test
    fun testIntL() {
        runTest {
            assertEquals(0, channel.availableForRead)
            channel.writeIntLittleEndian(-1)
            channel.flush()
            assertEquals(4, channel.availableForRead)
            assertEquals(-1, channel.readIntLittleEndian())
            assertEquals(0, channel.availableForRead)
        }
    }

    @Test
    fun testIntEdge() {
        runTest {
            for (shift in 1..3) {
                for (i in 1..shift) {
                    channel.writeByte(1)
                }

                repeat(Size / 4 - 1) {
                    channel.writeInt(0xeeeeeeeeL)
                }

                channel.flush()

                for (i in 1..shift) {
                    channel.readByte()
                }

                channel.writeInt(0x12345678)

                channel.flush()

                while (channel.availableForRead > 4) {
                    channel.readInt()
                }

                assertEquals(0x12345678, channel.readInt())
            }
        }
    }

    @Test
    fun testIntEdge2() {
        runTest {
            for (shift in 1..3) {
                for (i in 1..shift) {
                    channel.writeByte(1)
                }

                repeat(Size / 4 - 1) {
                    channel.writeInt(0xeeeeeeeeL)
                }

                channel.flush()

                for (i in 1..shift) {
                    channel.readByte()
                }

                channel.writeByte(0x12)
                channel.writeByte(0x34)
                channel.writeByte(0x56)
                channel.writeByte(0x78)

                channel.flush()

                while (channel.availableForRead > 4) {
                    channel.readInt()
                }

                assertEquals(0x12345678, channel.readInt())
            }
        }
    }


    @Test
    fun testLongB() {
        runTest {
            assertEquals(0, channel.availableForRead)
            channel.writeLong(Long.MIN_VALUE)
            channel.flush()
            assertEquals(8, channel.availableForRead)
            assertEquals(Long.MIN_VALUE, channel.readLong())
            assertEquals(0, channel.availableForRead)
        }
    }

    @Test
    fun testLongL() {
        runTest {
            assertEquals(0, channel.availableForRead)
            channel.writeLongLittleEndian(Long.MIN_VALUE)
            channel.flush()
            assertEquals(8, channel.availableForRead)
            assertEquals(Long.MIN_VALUE, channel.readLongLittleEndian())
            assertEquals(0, channel.availableForRead)
        }
    }

    @Test
    fun testLongEdge() {
        runTest {
            for (shift in 1..7) {
                for (i in 1..shift) {
                    channel.writeByte(1)
                }

                repeat(Size / 8 - 1) {
                    channel.writeLong(0x11112222eeeeeeeeL)
                }

                channel.flush()
                for (i in 1..shift) {
                    channel.readByte()
                }

                channel.writeLong(0x1234567812345678L)
                channel.flush()

                while (channel.availableForRead > 8) {
                    channel.readLong()
                }

                assertEquals(0x1234567812345678L, channel.readLong())
            }
        }
    }

    @Test
    fun testDoubleB() {
        runTest {
            assertEquals(0, channel.availableForRead)
            channel.writeDouble(1.05)
            channel.flush()

            assertEquals(8, channel.availableForRead)
            assertEquals(1.05, channel.readDouble())
            assertEquals(0, channel.availableForRead)
        }
    }

    @Test
    fun testDoubleL() {
        runTest {
            assertEquals(0, channel.availableForRead)
            channel.writeDoubleLittleEndian(1.05)
            channel.flush()

            assertEquals(8, channel.availableForRead)
            assertEquals(1.05, channel.readDoubleLittleEndian())
            assertEquals(0, channel.availableForRead)
        }
    }

    @Test
    fun testFloatB() {
        runTest {
            assertEquals(0, channel.availableForRead)
            channel.writeFloat(1.05f)
            channel.flush()

            assertEquals(4, channel.availableForRead)
            assertEquals(1.05f, channel.readFloat())
            assertEquals(0, channel.availableForRead)
        }
    }

    @Test
    fun testFloatL() {
        runTest {
            assertEquals(0, channel.availableForRead)
            channel.writeFloatLittleEndian(1.05f)
            channel.flush()

            assertEquals(4, channel.availableForRead)
            assertEquals(1.05f, channel.readFloatLittleEndian())
            assertEquals(0, channel.availableForRead)
        }
    }

    @Test
    fun testEndianMix() {
        val byteOrders = listOf(ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN)
        runTest {
            for (writeOrder in byteOrders) {
                for (readOrder in byteOrders) {
                    assertEquals(0, channel.availableForRead)
                    channel.writeShort(0x001f, writeOrder)
                    channel.flush()
                    if (writeOrder == readOrder)
                        assertEquals(0x001f, channel.readShort(readOrder))
                    else
                        assertEquals(0x1f00, channel.readShort(readOrder))

                    assertEquals(0, channel.availableForRead)
                    channel.writeShort(0x001f, writeOrder)
                    channel.flush()
                    if (writeOrder == readOrder)
                        assertEquals(0x001f, channel.readShort(readOrder))
                    else
                        assertEquals(0x1f00, channel.readShort(readOrder))

                    assertEquals(0, channel.availableForRead)
                    channel.writeInt(0x1f, writeOrder)
                    channel.flush()
                    if (writeOrder == readOrder)
                        assertEquals(0x0000001f, channel.readInt(readOrder))
                    else
                        assertEquals(0x1f000000, channel.readInt(readOrder))

                    assertEquals(0, channel.availableForRead)
                    channel.writeInt(0x1fL, writeOrder)
                    channel.flush()
                    if (writeOrder == readOrder)
                        assertEquals(0x0000001f, channel.readInt(readOrder))
                    else
                        assertEquals(0x1f000000, channel.readInt(readOrder))

                    assertEquals(0, channel.availableForRead)
                    channel.writeLong(0x1f, writeOrder)
                    channel.flush()
                    if (writeOrder == readOrder)
                        assertEquals(0x1f, channel.readLong(readOrder))
                    else
                        assertEquals(0x1f00000000000000L, channel.readLong(readOrder))
                }
            }
        }
    }

    @Test
    fun testClose() {
        runTest {
            channel.writeByte(1)
            channel.writeByte(2)
            channel.writeByte(3)

            channel.flush()
            assertEquals(1, channel.readByte())
            channel.close()

            assertEquals(2, channel.readByte())
            assertEquals(3, channel.readByte())

            try {
                channel.readByte()
                fail()
            } catch (expected: EOFException) {
            } catch (expected: NoSuchElementException) {
            }
        }
    }

    @Test
    fun testReadAndWriteFully() {
        runTest {
            val bytes = byteArrayOf(1, 2, 3, 4, 5)
            val dst = ByteArray(5)

            channel.writeFully(bytes)
            channel.flush()
            assertEquals(5, channel.availableForRead)
            channel.readFully(dst)
            assertTrue { dst.contentEquals(bytes) }

            channel.writeFully(bytes)
            channel.flush()

            val dst2 = ByteArray(4)
            channel.readFully(dst2)

            assertEquals(1, channel.availableForRead)
            assertEquals(5, channel.readByte())

            channel.close()

            try {
                channel.readFully(dst)
                fail("")
            } catch (expected: EOFException) {
            } catch (expected: NoSuchElementException) {
            }
        }
    }

    @Test
    fun testReadAndWritePartially() {
        runTest {
            val bytes = byteArrayOf(1, 2, 3, 4, 5)

            assertEquals(5, channel.writeAvailable(bytes))
            channel.flush()
            assertEquals(5, channel.readAvailable(ByteArray(100)))

            repeat(Size / bytes.size) {
                assertNotEquals(0, channel.writeAvailable(bytes))
                channel.flush()
            }

            channel.readAvailable(ByteArray(channel.availableForRead - 1))
            assertEquals(1, channel.readAvailable(ByteArray(100)))

            channel.close()
        }
    }

    @Test
    fun testPacket() = runTest {
        val packet = buildPacket {
            writeInt(0xffee)
            writeText("Hello")
        }

        channel.writeInt(packet.remaining)
        channel.writePacket(packet)

        channel.flush()

        val size = channel.readInt()
        val readed = channel.readPacket(size)

        assertEquals(0xffee, readed.readInt())
        assertEquals("Hello", readed.readUTF8Line())
    }

    @Test
    fun testBigPacket() = runTest {
        launch {
            val packet = buildPacket {
                writeInt(0xffee)
                writeText(".".repeat(8192))
            }

            channel.writeInt(packet.remaining)
            channel.writePacket(packet)

            channel.flush()
        }

        val size = channel.readInt()
        val readed = channel.readPacket(size)

        assertEquals(0xffee, readed.readInt())
        assertEquals(".".repeat(8192), readed.readUTF8Line())
    }

    @Test
    fun testWriteString() = runTest {
        channel.writeStringUtf8("abc")
        channel.close()

        assertEquals("abc", channel.readUTF8Line())
    }

    @Test
    fun testWriteCharSequence() = runTest {
        channel.writeStringUtf8("abc" as CharSequence)
        channel.close()

        assertEquals("abc", channel.readUTF8Line())
    }

    @Test
    fun testWriteSuspendable() = runTest {
        launch {
            expect(2)
            val bytes = ByteArray(10)
            channel.readFully(bytes, 0, 3)
            assertEquals("1 2 3 0 0 0 0 0 0 0", bytes.joinToString(separator = " ") { it.toString() })
            expect(3)

            channel.readFully(bytes, 3, 3)
            assertEquals("1 2 3 4 5 6 0 0 0 0", bytes.joinToString(separator = " ") { it.toString() })

            expect(5)
        }

        channel.writeSuspendSession {
            expect(1)
            val b1 = request(1)!!
            b1.writeFully(byteArrayOf(1, 2, 3))
            written(3)
            flush()

            yield()

            expect(4)
            val b2 = request(1)!!
            b2.writeFully(byteArrayOf(4, 5, 6))
            written(3)
            flush()
            yield()
        }

        finish(6)
    }

    @Test
    fun testWriteSuspendableWrap() = runTest {
        var read = 0
        var written = 0

        launch {
            val bytes = ByteArray(10)

            while (true) {
                val rc = channel.readAvailable(bytes)
                if (rc == -1) break
                read += rc
            }
        }

        channel.writeSuspendSession {
            val b1 = request(1)!!
            val size1 = b1.writeRemaining
            val ba = ByteArray(size1)
            repeat(size1) {
                ba[it] = (it % 64).toByte()
            }
            written = size1
            b1.writeFully(ba)
            written(size1)
            flush()

            assertNull(request(1))

            tryAwait(3)

            val b2 = request(3)!!
            b2.writeFully(byteArrayOf(1, 2, 3))
            written(3)
            written += 3
            flush()
            yield()
        }

        channel.close()
        yield()

        assertEquals(written, read)
    }

    @Test
    fun testBigTransfer(): Unit = runTest {
        val size = 262144 + 512

        launch {
            channel.writeFully(ByteArray(size))
            channel.close()
        }

        val packet = channel.readRemaining()
        try {
            assertEquals(size.toLong(), packet.remaining)
        } finally {
            packet.release()
        }
    }

    @Test
    fun testConstruct() = runTest {
        val channel = ByteReadChannel(ByteArray(2))
        channel.readRemaining().use { rem ->
            rem.discardExact(2)
        }
    }

    @Test
    fun testReadSuspendSessionAwait() = runTest {
        launch {
            expect(2)
            channel.writeInt(1)
            channel.flush()
            expect(3)
            channel.close()
        }

        var result: Boolean? = null
        channel.readSuspendableSession {
            expect(1)
            result = await(4000)
        }

        expect(4)
        assertEquals(false, result)
        assertEquals(4, channel.availableForRead)
        channel.discard(4)
        finish(5)
    }

    @Test
    fun testCompleteExceptionallyJob() = runTest {
        Job().also { channel.attachJob(it) }.completeExceptionally(IOException("Test exception"))

        assertFailsWith<IOException> { channel.readByte() }
    }

    private fun assertEquals(expected: Float, actual: Float) {
        if (abs(expected - actual) > 0.000001f) {
            kotlin.test.assertEquals(expected, actual)
        }
    }
}
