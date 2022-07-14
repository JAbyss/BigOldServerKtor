package com.foggyskies.chat.routes

import io.ktor.routing.*

fun Route.testRoute(){

//    get("/testRoute") {
//
//        // Getting the private field through reflection
//        val f = context::class.memberProperties.find { it.name == "call" }
//
//        f?.let {
//            // Making it accessible
//            it.isAccessible = true
//            val w = it.getter.call(context) as NettyApplicationCall
//
//            // Getting the remote address
//            val ip: SocketAddress? = w.request.context.pipeline().channel().remoteAddress()
//            println("IP: $ip")
//        }
//
////        val localHost = call.request.local.remoteHost
////        val localPort = call.request.local.port
////        println(localHost)
////        println(localPort)
////
////        val remoteHost = call.request.origin.remoteHost
////        val remotePort = call.request.origin.port
////        println(remoteHost)
////        println(remotePort)
//
//
//        call.respond(HttpStatusCode.OK)
//    }

}