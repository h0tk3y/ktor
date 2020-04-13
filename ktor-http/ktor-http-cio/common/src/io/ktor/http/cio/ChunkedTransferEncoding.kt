/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.http.cio

import io.ktor.http.cio.internals.*
import io.ktor.utils.io.*
import io.ktor.utils.io.bits.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.pool.*
import kotlinx.coroutines.*
import kotlin.coroutines.*
import kotlin.native.concurrent.*

/**
 * Encoder job type
 */
public typealias EncoderJob = ReaderJob

/**
 * Start chunked stream encoding coroutine
 */
public suspend fun encodeChunked(
    output: ByteWriteChannel,
    coroutineContext: CoroutineContext
): EncoderJob = GlobalScope.reader(coroutineContext, autoFlush = false) {
    encodeChunked(output, channel)
}

/**
 * Chunked stream encoding loop
 */
public suspend fun encodeChunked(output: ByteWriteChannel, input: ByteReadChannel) {
    try {
        while (true) {
            val count = input.read(DEFAULT_BYTE_BUFFER_SIZE) { source, startIndex, endIndex ->
                output.writeChunk(source, startIndex, endIndex)

                endIndex - startIndex
            }

            if (count <= 0) {
                output.writeFully(LastChunkBytes)
                break
            }
        }
        output.writeFully(LastChunkBytes)
        output.flush()
    } catch (cause: Throwable) {
        output.close(cause)
        input.cancel()
    }
}

private suspend inline fun ByteWriteChannel.writeChunk(chunk: Memory, startIndex: Int, endIndex: Int) {
    val size = endIndex - startIndex
    writeIntHex(size)
    writeShort(CrLfShort)
    writeFully(chunk, startIndex, endIndex)
    writeFully(CrLf)
    flush()
}
