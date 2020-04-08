/*
 * Copyright 2014-2020 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.network.util

import kotlinx.cinterop.*
import platform.posix.*

internal sealed class SocketAddress(
    val family: sa_family_t,
    val port: Int
) {
    internal abstract fun nativeAddress(block: (address: CPointer<sockaddr>, size: socklen_t) -> Unit)
}

internal class IPv4Address(
    family: sa_family_t,
    nativeAddress: in_addr,
    port: Int
) : SocketAddress(family, port) {
    private val ip: in_addr_t = nativeAddress.s_addr

    override fun nativeAddress(block: (address: CPointer<sockaddr>, size: socklen_t) -> Unit) {
        cValue<sockaddr_in> {
            sin_addr.s_addr = ip
            sin_port = port.convert()
            sin_family = family

            block(ptr.reinterpret(), sockaddr_in.size.convert())
        }
    }
}

internal class IPv6Address(
    family: sa_family_t,
    rawAddress: in6_addr,
    port: Int,
    private val flowInfo: uint32_t,
    private val scopeId: uint32_t
) : SocketAddress(family, port) {
//    private val ip = rawAddress.__u6_addr.readBytes(16)

    override fun nativeAddress(block: (address: CPointer<sockaddr>, size: socklen_t) -> Unit) {
        cValue<sockaddr_in6> {
            sin6_flowinfo = flowInfo
            sin6_family = family
            sin6_port = port.convert()
            sin6_scope_id = scopeId

            TODO()

            block(ptr.reinterpret(), sockaddr_in6.size.convert())
        }
    }
}
