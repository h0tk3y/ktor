package io.ktor.utils.io

import kotlinx.coroutines.*
import kotlinx.coroutines.CancellationException

/**
 * Thrown by cancellable suspending functions if the [Job] of the coroutine is cancelled while it is suspending.
 * It indicates _normal_ cancellation of a coroutine.
 * **It is not printed to console/log by default uncaught exception handler**.
 * (see [CoroutineExceptionHandler]).
 */
public typealias CancellationException = CancellationException
