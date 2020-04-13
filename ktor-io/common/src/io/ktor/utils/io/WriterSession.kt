package io.ktor.utils.io

import io.ktor.utils.io.bits.Memory
import io.ktor.utils.io.core.*
import io.ktor.utils.io.core.internal.ChunkBuffer

/**
 * Await for [desiredSpace] will be available for write and invoke [block] function providing [Memory] instance and
 * the corresponding range suitable for wiring in the memory. The block function should return number of bytes were
 * written, possibly 0.
 *
 * Similar to [ByteReadChannel.read], this function may invoke block function with lesser memory range when the
 * specified [desiredSpace] is bigger that the buffer's capacity
 * or when it is impossible to represent all [desiredSpace] bytes as a single memory range
 * due to internal implementation reasons.
 */
@ExperimentalIoApi
public suspend inline fun ByteWriteChannel.write(
    desiredSpace: Int = 1,
    block: (memory: Memory, startIndex: Long, endIndex: Long) -> Int
): Int {
    TODO()
}
