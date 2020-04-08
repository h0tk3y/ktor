/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.network.util

import io.ktor.util.*

public actual class NetworkAddress actual constructor(
    public val hostname: String,
    public val port: Int
) {
    internal val address: SocketAddress
        get() = resolvedRoutes.first()

    internal val resolvedRoutes = getAddressInfo(hostname, port)

    init {
        makeShared()
    }
}

public actual val NetworkAddress.hostname: String get() = hostname

public actual val NetworkAddress.port: Int get() = port

public actual val NetworkAddress.isResolved: Boolean
    get() = resolvedRoutes.isNotEmpty()

public actual class UnresolvedAddressException : IllegalArgumentException()
