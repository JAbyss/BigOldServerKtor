package com.foggyskies.server.plugin

import com.foggyskies.server.routes.auth.authRoutes
import com.foggyskies.server.routes.chat.chatSessionRoutes
import com.foggyskies.server.routes.cloudRoute
import com.foggyskies.server.routes.content.contentRoute
import com.foggyskies.server.routes.notificationRoutes
import com.foggyskies.server.routes.photoRouting
import com.foggyskies.server.routes.testRoute
import com.foggyskies.server.routes.user.usersRoutes
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    install(Routing) {
        usersRoutes()
        authRoutes()

        chatSessionRoutes()
        notificationRoutes()
        photoRouting()
        contentRoute()
        testRoute()
        cloudRoute()

        static("/") {
            files(".")
        }
    }
}