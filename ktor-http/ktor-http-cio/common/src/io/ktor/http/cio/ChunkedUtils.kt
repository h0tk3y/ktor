/*
 * Copyright 2014-2020 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.http.cio

import io.ktor.utils.io.core.*
import io.ktor.utils.io.pool.*
import kotlin.native.concurrent.*

internal const val MAX_CHUNK_SIZE_LENGTH = 128
internal const val CHUNK_BUFFER_POOL_SIZE = 2048
internal const val DEFAULT_BYTE_BUFFER_SIZE = 4088

@SharedImmutable
internal const val CrLfShort: Short = 0x0d0a

@SharedImmutable
internal val CrLf = "\r\n".toByteArray()

@SharedImmutable
internal val LastChunkBytes = "0\r\n\r\n".toByteArray()

@ThreadLocal
internal val ChunkSizeBufferPool: ObjectPool<StringBuilder> = object : DefaultPool<StringBuilder>(
    CHUNK_BUFFER_POOL_SIZE
) {
    override fun produceInstance(): StringBuilder = StringBuilder(MAX_CHUNK_SIZE_LENGTH)
    override fun clearInstance(instance: StringBuilder) = instance.apply { clear() }
}
