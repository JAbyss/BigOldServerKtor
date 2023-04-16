package com.foggyskies.server.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.net.SocketAddress
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun Route.testRoute(){

    get("/testRoute") {

        // Getting the private field through reflection
        val f = context::class.memberProperties.find { it.name == "call" }

        f?.let {
            // Making it accessible
            it.isAccessible = true
            val w = it.getter.call(context) as NettyApplicationCall

            // Getting the remote address
            val ip: SocketAddress? = w.request.context.pipeline().channel().remoteAddress()
            println("IP: $ip")
        }

        call.respond(HttpStatusCode.OK)
    }

}