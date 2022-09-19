package com.foggyskies.server.plugin

import com.foggyskies.server.databases.mongo.codes.testpacage.Logger
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
        try {
            val token = (isCheckToken ch ::checkToken) ?: throw error("")
            Logger.initLog(hashCode().toString(), method.value, path, isCheckToken, call.request.host(), token)

            body(this, token)
        } catch (e: Exception) {
            println(e)
//            Logger.addLog(idRequest = hashCode().toString(), e.toString(), status = Logger.StatusCodes.ERROR)
        } finally {
            Logger.saveLog(hashCode().toString())
        }
    }
}