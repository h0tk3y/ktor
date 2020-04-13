package io.ktor.utils.io

import io.ktor.utils.io.internal.*
import java.io.*
import java.nio.*

/**
 * Reads from the channel to the specified [dst] byte buffer until one of the following:
 * - channel's end
 * - [dst] capacity exceeded
 * - [delimiter] bytes encountered
 *
 * If [delimiter] bytes encountered then these bytes remain unconsumed
 *
 * @return non-negative number of copied bytes, possibly 0
 */
suspend fun ByteReadChannel.readUntilDelimiter(delimiter: ByteBuffer, dst: ByteBuffer): Int {
    require(delimiter.hasRemaining())
    require(delimiter !== dst)

    TODO()
}

suspend fun ByteReadChannel.skipDelimiter(delimiter: ByteBuffer) {
    require(delimiter.hasRemaining())
    TODO()
}

