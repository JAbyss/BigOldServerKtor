package com.foggyskies.server.routes.auth

import com.foggyskies.server.routes.auth.requests.*
import io.ktor.server.routing.*

fun Route.authRoutes() {

    blockAccount()
    generateCode()
    registration()
    auth()
    checkToken()
}