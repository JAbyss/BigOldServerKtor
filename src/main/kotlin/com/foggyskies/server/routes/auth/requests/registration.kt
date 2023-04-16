//package com.foggyskies.server.routes.auth.requests
//
//import com.foggyskies.server.databases.mongo.codes.models.VerifyCodeDC
//import com.foggyskies.server.databases.mongo.codes.testpacage.codes.CodesDataBase
//import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
//import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.createToken
//import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.insertUser
//import com.foggyskies.server.databases.mongo.main.models.RegistrationUserWithCodeDC
//import com.foggyskies.server.databases.mongo.main.models.Token
//import com.foggyskies.server.databases.mongo.main.models.UserMainEntity
//import com.foggyskies.server.plugin.SystemRouting
//import com.foggyskies.server.plugin.cRoute
//import com.foggyskies.server.routes.checkOnExistEmail
//import com.foggyskies.server.routes.checkOnExistUser
//import com.foggyskies.server.routes.checks
//import io.ktor.http.*
//import io.ktor.server.application.*
//import io.ktor.server.request.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//
//fun Route.registration() = cRoute(
//    SystemRouting.AuthRoute.registration,
//    HttpMethod.Post,
//    isCheckToken = false
//) {
//    val params = call.receive<RegistrationUserWithCodeDC>()
//
//    checks {
//        checkOnExistUser(username = params.username) ?: return@cRoute call.respondText(
//            status = HttpStatusCode.Conflict,
//            text = "Пользователь с таким логином уже существует."
//        )
//        checkOnExistEmail(e_mail = params.e_mail) ?: return@cRoute call.respondText(
//            status = HttpStatusCode.Conflict,
//            text = "Пользователь с таким e_mail уже существует."
//        )
//        getVerifyCode(params.e_mail) ?: return@cRoute call.respondText(
//            status = HttpStatusCode.Conflict,
//            text = "Неверный код."
//        )
//    }
//
//    val user = createUser(params)
//
//    val token = createNewToken(user.idUser)
//
//    call.respond(status = HttpStatusCode.Created, message = token)
//}
//
//private suspend inline fun createUser(params: RegistrationUserWithCodeDC): UserMainEntity {
//    val user = UserMainEntity(
//        username = params.username,
//        password = params.password,
//        e_mail = params.e_mail,
//        status = "Не в сети",
//    )
//    MainDataBase.Users.insertUser(user)
//    return user
//}
//
//private suspend fun getVerifyCode(email: String): VerifyCodeDC? {
//    val code = CodesDataBase.getCodeByID<VerifyCodeDC>(email)
//    code?.let {
//        CodesDataBase.deleteCode<VerifyCodeDC>(email)
//    }
//    return code
//}
//
//private suspend inline fun createNewToken(idUser: String): Token {
//    return MainDataBase.TokenCol.createToken(idUser)
//}
//TODO убрать