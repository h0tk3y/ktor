package io.ktor.utils.io

import io.ktor.utils.io.bits.*
import io.ktor.utils.io.core.*

/**
 * Channel for asynchronous writing of sequences of bytes.
 * This is a **single-writer channel**.
 *
 * Operations on this channel cannot be invoked concurrently, unless explicitly specified otherwise
 * in description. Exceptions are [close] and [flush].
 */
public expect interface ByteWriteChannel {
    /**
     * Returns number of bytes that can be written without suspension. Write operations do no suspend and return
     * immediately when this number is at least the number of bytes requested for write.
     */
    public val availableForWrite: Int

    /**
     * Returns `true` is channel has been closed and attempting to write to the channel will cause an exception.
     */
    public val isClosedForWrite: Boolean

    /**
     * Returns `true` if channel flushes automatically all pending bytes after every write function call.
     * If `false` then flush only happens at manual [flush] invocation or when the buffer is full.
     */
    public val autoFlush: Boolean

    /**
     * Number of bytes written to the channel.
     * It is not guaranteed to be atomic so could be updated in the middle of write operation.
     */
    public val totalBytesWritten: Long

    /**
     * An closure cause exception or `null` if closed successfully or not yet closed
     */
    public val closedCause: Throwable?

    /**
     * Writes as much as possible and only suspends if buffer is full
     */
    public suspend fun writeAvailable(source: ByteArray, offset: Int = 0, length: Int = source.size): Int

    /**
     * Writes all [source] bytes and suspends until all bytes written. Causes flush if buffer filled up or when [autoFlush]
     * Crashes if channel get closed while writing.
     */
    public suspend fun writeFully(source: ByteArray, offset: Int = 0, length: Int = source.size)

    /**
     * Writes all [source] bytes from [offset] using [length] and suspends until all bytes written.
     * Causes flush if buffer filled up or when [autoFlush]. Crashes if channel get closed while writing.
     */
    public suspend fun writeFully(source: Memory, offset: Int = 0, length: Int = source.size32)

    /**
     * Writes a [packet] fully or fails if channel get closed before the whole packet has been written
     */
    public suspend fun writePacket(packet: ByteReadPacket)

    /**
     * Writes long number and suspends until written.
     * Crashes if channel get closed while writing.
     */
    public suspend fun writeLong(value: Long)

    /**
     * Writes int number and suspends until written.
     * Crashes if channel get closed while writing.
     */
    public suspend fun writeInt(value: Int)

    /**
     * Writes short number and suspends until written.
     * Crashes if channel get closed while writing.
     */
    public suspend fun writeShort(value: Short)

    /**
     * Writes byte and suspends until written.
     * Crashes if channel get closed while writing.
     */
    public suspend fun writeByte(value: Byte)

    /**
     * Writes double number and suspends until written.
     * Crashes if channel get closed while writing.
     */
    public suspend fun writeDouble(value: Double)

    /**
     * Writes float number and suspends until written.
     * Crashes if channel get closed while writing.
     */
    public suspend fun writeFloat(value: Float)

    /**
     * Closes this channel with an optional exceptional [cause].
     * It flushes all pending write bytes (via [flush]).
     * This is an idempotent operation -- repeated invocations of this function have no effect and return `false`.
     *
     * A channel that was closed without a [cause], is considered to be _closed normally_.
     * A channel that was closed with non-null [cause] is called a _failed channel_. Attempts to read or
     * write on a failed channel throw this cause exception.
     *
     * After invocation of this operation [isClosedForWrite] starts returning `true` and
     * all subsequent write operations throw [ClosedWriteChannelException] or the specified [cause].
     * However, [isClosedForRead][ByteReadChannel.isClosedForRead] on the side of [ByteReadChannel]
     * starts returning `true` only after all written bytes have been read.
     *
     * Please note that if the channel has been closed with cause and it has been provided by [reader] or [writer]
     * coroutine then the corresponding coroutine will be cancelled with [cause]. If no [cause] provided then no
     * cancellation will be propagated.
     */
    public fun close(cause: Throwable? = null): Boolean

    /**
     * Flushes all pending write bytes making them available for read.
     *
     * This function is thread-safe and can be invoked in any thread at any time.
     * It does nothing when invoked on a closed channel.
     */
    public fun flush()
}

/**
 * Indicates attempt to write on [isClosedForWrite][ByteWriteChannel.isClosedForWrite] channel
 * that was closed without a cause. A _failed_ channel rethrows the original [close][ByteWriteChannel.close] cause
 * exception on send attempts.
 */
public class ClosedWriteChannelException(message: String?) : CancellationException(message)
