/*
 * Copyright 2014-2020 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.utils.io

import io.ktor.utils.io.bits.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.core.internal.*
import kotlinx.cinterop.*

internal class ByteChannelNative(
    override val autoFlush: Boolean = false,
    initial: ByteArray = EmptyByteArray
) : ByteChannel {
    override val totalBytesWritten: Long
        get() = TODO("Not yet implemented")

    override val closedCause: Throwable?
        get() = TODO("Not yet implemented")

    override suspend fun writeAvailable(source: ByteArray, offset: Int, length: Int): Int {
        TODO("Not yet implemented")
    }

    override suspend fun writeAvailable(src: CPointer<ByteVar>, offset: Int, length: Int): Int {
        TODO("Not yet implemented")
    }

    override suspend fun writeAvailable(src: CPointer<ByteVar>, offset: Long, length: Long): Int {
        TODO("Not yet implemented")
    }

    override suspend fun writeFully(source: ByteArray, offset: Int, length: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun writeFully(src: CPointer<ByteVar>, offset: Int, length: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun writeFully(src: CPointer<ByteVar>, offset: Long, length: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun writeFully(source: Memory, offset: Int, length: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun writePacket(packet: ByteReadPacket) {
        TODO("Not yet implemented")
    }

    override suspend fun writeLong(value: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun writeInt(value: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun writeShort(value: Short) {
        TODO("Not yet implemented")
    }

    override suspend fun writeByte(value: Byte) {
        TODO("Not yet implemented")
    }

    override suspend fun writeDouble(value: Double) {
        TODO("Not yet implemented")
    }

    override suspend fun writeFloat(value: Float) {
        TODO("Not yet implemented")
    }

    override fun close(cause: Throwable?): Boolean {
        TODO("Not yet implemented")
    }

    override fun flush() {
        TODO("Not yet implemented")
    }

    override fun attachJob(job: kotlinx.coroutines.Job) {
        TODO("Not yet implemented")
    }

    override val availableForRead: Int
        get() = TODO("Not yet implemented")
    override val isClosedForRead: Boolean
        get() = TODO("Not yet implemented")
    override val isClosedForWrite: Boolean
        get() = TODO("Not yet implemented")
    override val totalBytesRead: Long
        get() = TODO("Not yet implemented")

    override suspend fun readAvailable(
        destination: ByteArray,
        offset: Int,
        length: Int
    ): Int {
        TODO("Not yet implemented")
    }

    override suspend fun readAvailable(
        destination: CPointer<ByteVar>,
        offset: Int,
        length: Int
    ): Int {
        TODO("Not yet implemented")
    }

    override suspend fun readAvailable(
        destination: CPointer<ByteVar>,
        offset: Long,
        length: Long
    ): Int {
        TODO("Not yet implemented")
    }

    override suspend fun readFully(destination: ByteArray, offset: Int, length: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun readFully(
        destination: CPointer<ByteVar>,
        offset: Int,
        length: Int
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun readFully(
        destination: CPointer<ByteVar>,
        offset: Long,
        length: Long
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun readPacket(
        size: Int,
        headerSizeHint: Int
    ): io.ktor.utils.io.core.ByteReadPacket {
        TODO("Not yet implemented")
    }

    override suspend fun readRemaining(
        limit: Long,
        headerSizeHint: Int
    ): io.ktor.utils.io.core.ByteReadPacket {
        TODO("Not yet implemented")
    }

    override suspend fun readLong(): Long {
        TODO("Not yet implemented")
    }

    override suspend fun readInt(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun readShort(): kotlin.Short {
        TODO("Not yet implemented")
    }

    override suspend fun readByte(): kotlin.Byte {
        TODO("Not yet implemented")
    }

    override suspend fun readBoolean(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun readDouble(): kotlin.Double {
        TODO("Not yet implemented")
    }

    override suspend fun readFloat(): kotlin.Float {
        TODO("Not yet implemented")
    }

    override suspend fun <A : kotlin.text.Appendable> readUTF8LineTo(out: A, limit: Int): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun readUTF8Line(limit: Int): kotlin.String? {
        TODO("Not yet implemented")
    }

    override fun cancel(cause: kotlin.Throwable?): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun discard(max: Long): Long {
        TODO("Not yet implemented")
    }

    override suspend fun peekTo(
        destination: io.ktor.utils.io.bits.Memory,
        destinationOffset: Long,
        offset: Long,
        min: Long,
        max: Long
    ): Long {
        TODO("Not yet implemented")
    }

    override val availableForWrite: Int
        get() = TODO("Not yet implemented")
}
