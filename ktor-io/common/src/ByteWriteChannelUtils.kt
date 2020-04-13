/*
 * Copyright 2014-2020 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import io.ktor.utils.io.*
import io.ktor.utils.io.core.*

/**
 * Write integer [value] as short.
 */
public suspend fun ByteWriteChannel.writeShort(value: Int) {
    writeShort((value and 0xffff).toShort())
}

/**
 * Write integer [value] as short using [byteOrder].
 */
public suspend fun ByteWriteChannel.writeShort(value: Int, byteOrder: ByteOrder) {
    writeShort((value and 0xffff).toShort(), byteOrder)
}

/**
 * Write integer [value] as byte.
 */
public suspend fun ByteWriteChannel.writeByte(value: Int) {
    writeByte((value and 0xff).toByte())
}

/**
 * Write long [value] as integer.
 */
public suspend fun ByteWriteChannel.writeInt(value: Long) {
    writeInt(value.toInt())
}

/**
 * Write integer [value] using [byteOrder].
 */
public suspend fun ByteWriteChannel.writeInt(value: Long, byteOrder: ByteOrder) {
    writeInt(value.toInt(), byteOrder)
}

/**
 * Write string [value].
 */
public suspend fun ByteWriteChannel.writeStringUtf8(value: CharSequence) {
    val packet = buildPacket {
        writeText(value)
    }

    return writePacket(packet)
}

/**
 * Write boolean [value].
 */
public suspend fun ByteWriteChannel.writeBoolean(value: Boolean) {
    writeByte(if (value) 1 else 0)
}

/**
 * Writes UTF16 character.
 */
public suspend fun ByteWriteChannel.writeChar(value: Char) {
    writeShort(value.toInt())
}

/**
 * Write packet using [builder].
 */
public suspend inline fun ByteWriteChannel.writePacket(
    headerSizeHint: Int = 0,
    builder: BytePacketBuilder.() -> Unit
) {
    writePacket(buildPacket(headerSizeHint, builder))
}

