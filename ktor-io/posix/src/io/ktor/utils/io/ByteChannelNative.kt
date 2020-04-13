/*
 * Copyright 2014-2020 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.utils.io

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

    override val availableForRead: kotlin.Int
        get() = TODO("Not yet implemented")
    override val isClosedForRead: kotlin.Boolean
        get() = TODO("Not yet implemented")
    override val isClosedForWrite: kotlin.Boolean
        get() = TODO("Not yet implemented")
    override var readByteOrder: io.ktor.utils.io.core.ByteOrder
        get() = TODO("Not yet implemented")
        set(value) {}
    override val totalBytesRead: kotlin.Long
        get() = TODO("Not yet implemented")

    override suspend fun readAvailable(dst: kotlin.ByteArray, offset: kotlin.Int, length: kotlin.Int): kotlin.Int {
        TODO("Not yet implemented")
    }

    override suspend fun readAvailable(dst: Buffer): kotlin.Int {
        TODO("Not yet implemented")
    }

    override suspend fun readAvailable(
        dst: kotlinx.cinterop.CPointer<kotlinx.cinterop.ByteVar>,
        offset: kotlin.Int,
        length: kotlin.Int
    ): kotlin.Int {
        TODO("Not yet implemented")
    }

    override suspend fun readAvailable(
        dst: kotlinx.cinterop.CPointer<kotlinx.cinterop.ByteVar>,
        offset: kotlin.Long,
        length: kotlin.Long
    ): kotlin.Int {
        TODO("Not yet implemented")
    }

    override suspend fun readFully(dst: kotlin.ByteArray, offset: kotlin.Int, length: kotlin.Int) {
        TODO("Not yet implemented")
    }

    override suspend fun readFully(dst: Buffer, n: kotlin.Int) {
        TODO("Not yet implemented")
    }

    override suspend fun readFully(
        dst: kotlinx.cinterop.CPointer<kotlinx.cinterop.ByteVar>,
        offset: kotlin.Int,
        length: kotlin.Int
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun readFully(
        dst: kotlinx.cinterop.CPointer<kotlinx.cinterop.ByteVar>,
        offset: kotlin.Long,
        length: kotlin.Long
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun readPacket(
        size: kotlin.Int,
        headerSizeHint: kotlin.Int
    ): io.ktor.utils.io.core.ByteReadPacket {
        TODO("Not yet implemented")
    }

    override suspend fun readRemaining(
        limit: kotlin.Long,
        headerSizeHint: kotlin.Int
    ): io.ktor.utils.io.core.ByteReadPacket {
        TODO("Not yet implemented")
    }

    override suspend fun readLong(): kotlin.Long {
        TODO("Not yet implemented")
    }

    override suspend fun readInt(): kotlin.Int {
        TODO("Not yet implemented")
    }

    override suspend fun readShort(): kotlin.Short {
        TODO("Not yet implemented")
    }

    override suspend fun readByte(): kotlin.Byte {
        TODO("Not yet implemented")
    }

    override suspend fun readBoolean(): kotlin.Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun readDouble(): kotlin.Double {
        TODO("Not yet implemented")
    }

    override suspend fun readFloat(): kotlin.Float {
        TODO("Not yet implemented")
    }

    override suspend fun <A : kotlin.text.Appendable> readUTF8LineTo(out: A, limit: kotlin.Int): kotlin.Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun readUTF8Line(limit: kotlin.Int): kotlin.String? {
        TODO("Not yet implemented")
    }

    override fun cancel(cause: kotlin.Throwable?): kotlin.Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun discard(max: kotlin.Long): kotlin.Long {
        TODO("Not yet implemented")
    }

    override suspend fun peekTo(
        destination: io.ktor.utils.io.bits.Memory,
        destinationOffset: kotlin.Long,
        offset: kotlin.Long,
        min: kotlin.Long,
        max: kotlin.Long
    ): kotlin.Long {
        TODO("Not yet implemented")
    }

    override val availableForWrite: kotlin.Int
        get() = TODO("Not yet implemented")
}
