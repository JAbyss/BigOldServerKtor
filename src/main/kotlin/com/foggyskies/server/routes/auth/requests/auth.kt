package com.foggyskies.server.routes.auth.requests

import com.foggyskies.PasswordCoder
import com.foggyskies.server.databases.mongo.codes.testpacage.codes.Codes
import com.foggyskies.server.databases.mongo.codes.models.LockCodeDC
import com.foggyskies.server.databases.mongo.codes.testpacage.codes.CodesDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.checkOnExistTokenById
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.createToken
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getTokenById
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getUserByUsername
import com.foggyskies.server.databases.mongo.main.models.LoginUserDC
import com.foggyskies.server.databases.mongo.main.models.Token
import com.foggyskies.server.databases.mongo.main.models.UserMainEntity
import com.foggyskies.server.extendfun.generateUUID
import com.foggyskies.server.extendfun.ip
import com.foggyskies.server.plugin.EmailSender
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.plugin.getLocation
import com.foggyskies.server.routes.checks
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

fun Route.auth() =
    cRoute(
        SystemRouting.AuthRoute.auth,
        HttpMethod.Post,
        isCheckToken = false
    ) { _ ->
        val params: LoginUserDC = call.receive()

        lateinit var user: UserMainEntity

        checks {
            user = getUserByUsername(params.username)
                ?: return@cRoute call.respond(status = HttpStatusCode.NotFound, "Пользователь не найден.")
            checkPasswordOnCorrect(user.password, params.password)
                ?: return@cRoute call.respond(status = HttpStatusCode.Conflict, "Пароль не верный.")
        }

        if (user.isLocked) return@cRoute call.respond(status = HttpStatusCode.Conflict, "Пользователь заблокирован.")

        val token =
            if (!checkOnExistToken(user.idUser))
                createNewToken(user.idUser)
            else
                getToken(user.idUser)

        call.respond(message = token, status = HttpStatusCode.OK)

//        println(call.request.host() != "0.0.0.0" || call.request.host() != "localhost")
        if (call.request.host() != "0.0.0.0" && call.request.host() != "localhost")
            getInfoAboutConnect(user)
    }


private suspend inline fun checkOnExistToken(idUser: String): Boolean {
    return MainDataBase.TokenCol.checkOnExistTokenById(idUser)
}

private suspend inline fun createNewToken(idUser: String): Token {
    return MainDataBase.TokenCol.createToken(idUser)
}

private suspend inline fun getToken(idUser: String): Token {
    return MainDataBase.TokenCol.getTokenById(idUser)
}

private suspend inline fun PipelineContext<Unit, ApplicationCall>.getInfoAboutConnect(user: UserMainEntity) {
    ip?.let {
        val location = it.getLocation()
        val recoveryCode = generateUUID(30)

        val code = CodesDataBase.getCodeByIdUser<LockCodeDC>(user.idUser)
        code ?: insertCodeInDb(recoveryCode, user.idUser)

        EmailSender.sendNotificationSignInOnAccount(location, user.e_mail, code?.id ?: recoveryCode)
    }
}

private suspend inline fun insertCodeInDb(recoveryCode: String, idUser: String) {

    CodesDataBase.insertCodes(
        LockCodeDC(
            id = recoveryCode,
            idUser = idUser,
            lock_code = Codes.Block.SING_IN.name
        )
    )
}

private suspend inline fun getUserByUsername(username: String): UserMainEntity? {
    return MainDataBase.Users.getUserByUsername(username)
}

private fun checkPasswordOnCorrect(password: String, userPassword: String): Unit? {
    return if (PasswordCoder.decodeStringFS(password) == PasswordCoder.decodeStringFS(userPassword)) Unit else null
}