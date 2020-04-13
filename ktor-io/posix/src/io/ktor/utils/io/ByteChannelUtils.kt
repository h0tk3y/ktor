package io.ktor.utils.io

import io.ktor.utils.io.core.*
import io.ktor.utils.io.internal.*


/**
 * Creates buffered channel for asynchronous reading and writing of sequences of bytes.
 */
public actual fun ByteChannel(autoFlush: Boolean): ByteChannel {
    return ByteChannelNative(autoFlush)
}

/**
 * Creates channel for reading from the specified byte array.
 */
public actual fun ByteReadChannel(
    content: ByteArray,
    offset: Int,
    length: Int
): ByteReadChannel {
    require(offset >= 0)
    require(length >= 0)
    require(offset + length <= content.size)

    if (content.isEmpty() || length == 0) {
        return ByteReadChannel.Empty
    }

    val initial = content.copyOfRange(offset, offset + length)
    return ByteChannelNative(autoFlush = false, initial)
}

public actual suspend fun ByteReadChannel.joinTo(
    dst: ByteWriteChannel, closeOnEnd: Boolean
) {
    (this as ByteChannelSequentialBase).joinToImpl((dst as ByteChannelSequentialBase), closeOnEnd)
}

/**
 * Reads up to [limit] bytes from receiver channel and writes them to [dst] channel.
 * Closes [dst] channel if fails to read or write with cause exception.
 * @return a number of copied bytes
 */
public actual suspend fun ByteReadChannel.copyTo(dst: ByteWriteChannel, limit: Long): Long {
    return (this as ByteChannelSequentialBase).copyToSequentialImpl((dst as ByteChannelSequentialBase), limit)
}

