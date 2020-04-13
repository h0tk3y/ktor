package io.ktor.utils.io

import io.ktor.utils.io.bits.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import io.ktor.utils.io.core.*
import java.nio.ByteBuffer

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
@ExperimentalIoApi
class ByteChannelSequentialJVM(initial: IoBuffer, autoFlush: Boolean) : ByteChannelSequentialBase(initial, autoFlush) {

    @Volatile
    private var attachedJob: Job? = null

    @OptIn(InternalCoroutinesApi::class)
    override fun attachJob(job: Job) {
        attachedJob?.cancel()
        attachedJob = job
        job.invokeOnCompletion(onCancelling = true) { cause ->
            attachedJob = null
            if (cause != null) {
                cancel(cause)
            }
        }
    }

    override suspend fun writeAvailable(source: ByteBuffer): Int {
        val rc = tryWriteAvailable(source)
        return when {
            rc > 0 -> rc
            !source.hasRemaining() -> 0
            else -> writeAvailableSuspend(source)
        }
    }

    private suspend fun writeAvailableSuspend(src: ByteBuffer): Int {
        awaitFreeSpace()
        return writeAvailable(src)
    }

    override suspend fun writeFully(source: ByteBuffer) {
        tryWriteAvailable(source)
        if (!source.hasRemaining()) return

        writeFullySuspend(source)
    }

    override suspend fun writeFully(source: Memory, offset: Int, length: Int) {
        TODO("Not yet implemented")
    }

    private suspend fun writeFullySuspend(src: ByteBuffer) {
        while (src.hasRemaining()) {
            awaitFreeSpace()
            tryWriteAvailable(src)
        }
    }

    private fun tryWriteAvailable(src: ByteBuffer): Int {
        val srcRemaining = src.remaining()
        val availableForWrite = availableForWrite

        return when {
            closed -> throw closedCause ?: ClosedSendChannelException("Channel closed for write")
            srcRemaining == 0 -> 0
            srcRemaining <= availableForWrite -> {
                writable.writeFully(src)
                srcRemaining
            }
            availableForWrite == 0 -> 0
            else -> {
                val oldLimit = src.limit()
                src.limit(src.position() + availableForWrite)
                writable.writeFully(src)
                src.limit(oldLimit)
                availableForWrite
            }
        }
    }

    override suspend fun readAvailable(destination: ByteBuffer): Int {
        val rc = tryReadAvailable(destination)
        if (rc != 0) return rc
        if (!destination.hasRemaining()) return 0
        return readAvailableSuspend(destination)
    }

    private suspend fun readAvailableSuspend(dst: ByteBuffer): Int {
        if (!await(1)) return -1
        return readAvailable(dst)
    }

    override suspend fun readFully(destination: ByteBuffer): Int {
        val rc = tryReadAvailable(destination)
        if (rc == -1) throw EOFException("Channel closed")
        if (!destination.hasRemaining()) return rc

        return readFullySuspend(destination, rc)
    }

    private suspend fun readFullySuspend(dst: ByteBuffer, rc0: Int): Int {
        var count = rc0

        while (dst.hasRemaining()) {
            if (!await(1)) throw EOFException("Channel closed")
            val rc = tryReadAvailable(dst)
            if (rc == -1) throw EOFException("Channel closed")
            count += rc
        }

        return count
    }

    private fun tryReadAvailable(dst: ByteBuffer): Int {
        val closed = closed
        val closedCause = closedCause

        return when {
            closedCause != null -> throw closedCause
            closed -> {
                readable.readAvailable(dst).takeIf { it != 0 }.also { afterRead() } ?: -1
            }
            else -> readable.readAvailable(dst).also { afterRead() }
        }
    }

    @Deprecated("Binary compatibility.", level = DeprecationLevel.HIDDEN)
    override suspend fun consumeEachBufferRange(visitor: ConsumeEachBufferVisitor) {
        val readable = readable
        var invokedWithLast = false

        while (true) {
            readable.readDirect(1) { bb: ByteBuffer ->
                val last = closed && bb.remaining() == availableForRead
                visitor(bb, last)
                if (last) {
                    invokedWithLast = true
                }
            }
            if (!await(1)) break
        }

        if (!invokedWithLast) {
            visitor(ByteBuffer.allocate(0), true)
        }
    }

    override suspend fun read(min: Int, consumer: (ByteBuffer) -> Unit) {
        require(min >= 0)

        if (!await(min)) throw EOFException("Channel closed while $min bytes expected")

        readable.readDirect(min) { bb ->
            consumer(bb)
        }
    }

    override suspend fun write(min: Int, block: (ByteBuffer) -> Unit) {
        if (closed) {
            throw closedCause ?: ClosedSendChannelException("Channel closed for write")
        }
        writable.writeDirect(min) { block(it) }
        awaitFreeSpace()
    }

    override suspend fun writeWhile(block: (ByteBuffer) -> Boolean) {
        while (true) {
            if (closed) {
                throw closedCause ?: ClosedSendChannelException("Channel closed for write")
            }

            var cont = false
            writable.writeDirect(1) {
                cont = block(it)
            }

            awaitFreeSpace()

            if (!cont) break
        }
    }
}

