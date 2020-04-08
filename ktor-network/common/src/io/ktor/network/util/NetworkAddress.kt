package io.ktor.network.util

public expect class NetworkAddress(hostname: String, port: Int)

public expect val NetworkAddress.hostname: String
public expect val NetworkAddress.port: Int

public expect val NetworkAddress.isResolved: Boolean

public expect class UnresolvedAddressException() : IllegalArgumentException
