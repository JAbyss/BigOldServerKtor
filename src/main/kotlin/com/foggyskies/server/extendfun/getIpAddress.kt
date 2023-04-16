package com.foggyskies.server.extendfun

import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.websocket.*
import io.ktor.util.pipeline.*
import java.net.SocketAddress
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun PipelineContext<Unit, ApplicationCall>.getIpAddress(): SocketAddress? {
    val f = context::class.memberProperties.find { it.name == "call" }

    f?.let {
        // Making it accessible
        it.isAccessible = true
        val w = it.getter.call(context) as NettyApplicationCall
//        println(w.request.context.pipeline().channel().remoteAddress())

        // Getting the remote address
        return w.request.context.pipeline().channel().remoteAddress()
    }
    return null
}

val PipelineContext<Unit, ApplicationCall>.ip: SocketAddress?
    get() = run {
        val f = context::class.memberProperties.find { it.name == "call" }

        f?.let {
            // Making it accessible
            it.isAccessible = true
            val w = it.getter.call(context) as NettyApplicationCall
            // Getting the remote address
            return w.request.context.pipeline().channel().remoteAddress()
        }
        return null
    }

fun DefaultWebSocketServerSession.getIpAddress(): SocketAddress? {

    val f = call::class.memberProperties.find { it.name == "call" }

    f?.let {
        // Making it accessible
        it.isAccessible = true
        val w = it.getter.call(call) as NettyApplicationCall

        // Getting the remote address
        println(w.request.context.pipeline().channel().remoteAddress())
        return w.request.context.pipeline().channel().remoteAddress()
    }
    return null
}