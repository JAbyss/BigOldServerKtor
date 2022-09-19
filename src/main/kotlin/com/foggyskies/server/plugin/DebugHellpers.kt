package com.foggyskies.server.plugin

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

suspend fun leadTimeMillis(name: String, action: suspend () -> Unit) {
    println("$name, время выполнения: " + measureTimeMillis(action) + " ms")
}

suspend fun leadTimeNanos(name: String, action: suspend () -> Unit) {
    println("$name, время выполнения: " + measureTimeNanos(action) + " ns")
}

@OptIn(ExperimentalContracts::class)
suspend inline fun measureTimeMillis(crossinline block: suspend () -> Unit): Long {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val start = System.currentTimeMillis()
    block()
    return System.currentTimeMillis() - start
}

@OptIn(ExperimentalContracts::class)
suspend inline fun measureTimeNanos(crossinline block: suspend () -> Unit): Long {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val start = System.nanoTime()
    block()
    return System.nanoTime() - start
}