package kotlinx.coroutines.experimental.io

import io.ktor.utils.io.*
import kotlin.test.*

class ByteBufferChannelLookAheadTest : ByteChannelTestBase() {
    @Test
    fun testDoNothing() = runTest {
        channel.lookAheadSuspend {
        }
    }

    @Test
    fun testDoNothingWhileWriting() = runTest {
        channel.writeSuspendSession {
            channel.lookAheadSuspend {
            }
        }
    }

    @Test
    fun testDoNothingWhileWriting2() = runTest {
        channel.lookAheadSuspend {
            channel.writeSuspendSession {
            }
        }
    }

    @Test
    fun testReadDuringWriting() = runTest {
        channel.writeSuspendSession {
            channel.lookAheadSuspend {
                this@writeSuspendSession.request(1)!!.writeInt(777)
                written(4)
                flush()

                val bb = request(0, 1)
                assertNotNull(bb)
                assertEquals(777, bb.getInt())
                consumed(4)
            }
        }
    }
}
