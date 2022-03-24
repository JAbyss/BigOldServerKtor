package com.foggyskies.chat.extendfun

suspend fun <T> Iterable<T>.forEachSuspend(action: suspend (T) -> Unit): Unit {
    for (element in this) action(element)
}