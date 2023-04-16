//package com.foggyskies.server.routes.auth.requests
//
//import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
//import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.checkOnExistToken
//import com.foggyskies.server.plugin.SystemRouting
//import com.foggyskies.server.plugin.cRoute
//import io.ktor.http.*
//import io.ktor.server.application.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//
//fun Route.checkToken() = cRoute(
//    SystemRouting.AuthRoute.checkToken,
//    HttpMethod.Get,
//    isCheckToken = true
//){token ->
//
//    if (checkToken(token.token))
//        call.respond(HttpStatusCode.OK)
//    else
//        call.respond(HttpStatusCode.NotFound)
//}
//
//private suspend inline fun checkToken(token: String): Boolean {
//    return MainDataBase.TokenCol.checkOnExistToken(token)
//}

//TODO Убрать