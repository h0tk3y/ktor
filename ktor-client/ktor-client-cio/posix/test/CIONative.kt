/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.util.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlin.coroutines.*
import kotlin.native.concurrent.*
import kotlin.test.*

class CIONative {

    @Test
    fun testGoogle(): Unit = runBlocking {
        HttpClient(CIO).use { client ->
            val result = client.get<String>("http://www.google.ru/")
            println(result)
            Unit
        }
    }

    class Y : CoroutineScope {
        override val coroutineContext: CoroutineContext by lazy {
            SilentSupervisor() + Dispatchers.Unconfined + CoroutineName("context")
        }

        fun y(callContext: CoroutineContext): Deferred<CoroutineScope> {
            println(coroutineContext)
            println(callContext)
            return async(
                callContext + CoroutineName("DedicatedRequest")
            ) {
                freeze()
            }
        }
    }

    @Test
    fun x(): Unit = runBlocking {
        val x = Job(Job().freeze())
//        createCallContext(Job()).freeze()
//        Unit
    }
}

@ThreadLocal
internal val CALL_COROUTINE = CoroutineName("call-context")

internal suspend fun createCallContext(parentJob: Job): CoroutineContext {
    val callJob = Job(parentJob)
    attachToUserJob(callJob)

    return functionContext() + callJob + CALL_COROUTINE
}

@OptIn(InternalCoroutinesApi::class)
internal suspend inline fun attachToUserJob(callJob: Job) {
    val userJob = coroutineContext[Job] ?: return

    val cleanupHandler = userJob.invokeOnCompletion(onCancelling = true) { cause ->
        cause ?: return@invokeOnCompletion
        callJob.cancel(CancellationException(cause.message))
    }

    callJob.invokeOnCompletion {
        cleanupHandler.dispose()
    }
}

private suspend inline fun functionContext(): CoroutineContext = coroutineContext
