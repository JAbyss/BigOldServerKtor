package com.foggyskies.server.plugin

import com.foggyskies.server.databases.mongo.testpacage.Logger
import com.foggyskies.server.databases.mongo.main.models.Token
import com.foggyskies.server.infixfun.ch
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import com.foggyskies.server.plugin.verifications.checkToken

fun Route.cRoute(
    path: String,
    method: HttpMethod,
    isCheckToken: Boolean = false,
    body: suspend PipelineContext<Unit, ApplicationCall>.(Token) -> Unit
) = this.route(path, method) {
    handle {
        com.foggyskies.server.databases.mongo.testpacage.Logger.initLog(hashCode().toString(), method.value, path, isCheckToken, call.request.host(), null)
        var token: Token? = null
        try {
            token = if (isCheckToken)
                checkToken()
            else
                Token.Empty
//            val token = (isCheckToken ch ::checkToken) ?: throw error("")

            body(this, token)
        } catch (e: Exception) {
            println(e)
            com.foggyskies.server.databases.mongo.testpacage.Logger.addLog(idRequest = hashCode().toString(), e.toString(), status = com.foggyskies.server.databases.mongo.testpacage.Logger.StatusCodes.ERROR)
        } finally {
            com.foggyskies.server.databases.mongo.testpacage.Logger.saveLog(hashCode().toString(), token)
        }
    }
}

fun Route.cRoute(
    path: String,
    method: HttpMethod,
    body: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit
) = this.route(path, method) {
    handle {
        com.foggyskies.server.databases.mongo.testpacage.Logger.initLog(hashCode().toString(), method.value, path, false, call.request.host(), null)
        try {
//            val token = (isCheckToken ch ::checkToken) ?: throw error("")

            body(this)
        } catch (e: Exception) {
            println(e)
            com.foggyskies.server.databases.mongo.testpacage.Logger.addLog(idRequest = hashCode().toString(), e.toString(), status = com.foggyskies.server.databases.mongo.testpacage.Logger.StatusCodes.ERROR)
        } finally {
            com.foggyskies.server.databases.mongo.testpacage.Logger.saveLog(hashCode().toString(), null)
        }
    }
}