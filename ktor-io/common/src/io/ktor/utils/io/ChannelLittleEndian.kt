package io.ktor.utils.io

import io.ktor.utils.io.bits.*
import io.ktor.utils.io.core.*

public suspend inline fun ByteReadChannel.readShort(byteOrder: ByteOrder): Short =
    readShort().reverseIfNeeded(byteOrder) { reverseByteOrder() }

public suspend inline fun ByteReadChannel.readInt(byteOrder: ByteOrder): Int =
    readInt().reverseIfNeeded(byteOrder) { reverseByteOrder() }

public suspend inline fun ByteReadChannel.readLong(byteOrder: ByteOrder): Long =
    readLong().reverseIfNeeded(byteOrder) { reverseByteOrder() }

public suspend inline fun ByteReadChannel.readFloat(byteOrder: ByteOrder): Float {
    return readFloat().reverseIfNeeded(byteOrder) { reverseByteOrder() }
}

public suspend inline fun ByteReadChannel.readDouble(byteOrder: ByteOrder): Double {
    return readDouble().reverseIfNeeded(byteOrder) { reverseByteOrder() }
}

public suspend inline fun ByteReadChannel.readShortLittleEndian(): Short {
    return readShort(ByteOrder.LITTLE_ENDIAN)
}

public suspend inline fun ByteReadChannel.readIntLittleEndian(): Int {
    return readInt(ByteOrder.LITTLE_ENDIAN)
}

public suspend inline fun ByteReadChannel.readLongLittleEndian(): Long {
    return readLong(ByteOrder.LITTLE_ENDIAN)
}

public suspend inline fun ByteReadChannel.readFloatLittleEndian(): Float {
    return readFloat(ByteOrder.LITTLE_ENDIAN)
}

public suspend inline fun ByteReadChannel.readDoubleLittleEndian(): Double {
    return readDouble(ByteOrder.LITTLE_ENDIAN)
}

public suspend fun ByteWriteChannel.writeShort(value: Short, byteOrder: ByteOrder) {
    writeShort(value.reverseIfNeeded(byteOrder) { reverseByteOrder() })
}

public suspend fun ByteWriteChannel.writeInt(value: Int, byteOrder: ByteOrder) {
    writeInt(value.reverseIfNeeded(byteOrder) { reverseByteOrder() })
}

public suspend fun ByteWriteChannel.writeLong(value: Long, byteOrder: ByteOrder) {
    writeLong(value.reverseIfNeeded(byteOrder) { reverseByteOrder() })
}

public suspend fun ByteWriteChannel.writeFloat(value: Float, byteOrder: ByteOrder) {
    writeFloat(value.reverseIfNeeded(byteOrder) { reverseByteOrder() })
}

public suspend fun ByteWriteChannel.writeDouble(value: Double, byteOrder: ByteOrder) {
    writeDouble(value.reverseIfNeeded(byteOrder) { reverseByteOrder() })
}

public suspend fun ByteWriteChannel.writeShortLittleEndian(value: Short) {
    writeShort(value, ByteOrder.LITTLE_ENDIAN)
}

public suspend fun ByteWriteChannel.writeIntLittleEndian(value: Int) {
    writeInt(value, ByteOrder.LITTLE_ENDIAN)
}

public suspend fun ByteWriteChannel.writeLongLittleEndian(value: Long) {
    writeLong(value, ByteOrder.LITTLE_ENDIAN)
}

public suspend fun ByteWriteChannel.writeFloatLittleEndian(value: Float) {
    writeFloat(value, ByteOrder.LITTLE_ENDIAN)
}

public suspend fun ByteWriteChannel.writeDoubleLittleEndian(value: Double) {
    writeDouble(value, ByteOrder.LITTLE_ENDIAN)
}

@PublishedApi
internal inline fun <T> T.reverseIfNeeded(byteOrder: ByteOrder, reverseBlock: T.() -> T): T {
    return when (byteOrder) {
        ByteOrder.BIG_ENDIAN -> this
        else -> reverseBlock()
    }
}
