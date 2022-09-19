package com.foggyskies.server.routes.auth.requests

import com.foggyskies.server.databases.mongo.codes.models.VerifyCodeDC
import com.foggyskies.server.databases.mongo.codes.testpacage.codes.CodesDataBase
import com.foggyskies.server.databases.mongo.main.models.RegistrationUserDC
import com.foggyskies.server.extendfun.generateUUID
import com.foggyskies.server.plugin.EmailSender
import com.foggyskies.server.plugin.Settings.Durations.verify
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.TaskManager
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.routes.checkOnExistEmail
import com.foggyskies.server.routes.checkOnExistUser
import com.foggyskies.server.routes.checks
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.generateCode() = cRoute(
    SystemRouting.AuthRoute.generateCode,
    HttpMethod.Post,
    isCheckToken = false
) {
    val params = call.receive<RegistrationUserDC>()

    checks {
        checkOnExistUser(params.username) ?: return@cRoute call.respondText(
            status = HttpStatusCode.Conflict,
            text = "Пользователь с таким логином уже существует."
        )
        checkOnExistEmail(params.e_mail) ?: return@cRoute call.respondText(
            status = HttpStatusCode.Conflict,
            text = "Пользователь с таким e_mail уже существует."
        )
    }

    val codeConfirmation = generateUUID(4)

    startTask(codeConfirmation, params.e_mail)

    call.respondText(status = HttpStatusCode.Created, text = "Код создан.")
}


private suspend fun startTask(code: String, e_mail: String) {
    CodesDataBase.getCodeByID<VerifyCodeDC>(e_mail) ?:
    TaskManager.addTask(TaskManager.Task(code = code, duration = verify, before_action = {
        CodesDataBase.insertCodes(VerifyCodeDC(e_mail, code))
        EmailSender.sendCode(e_mail, code)
    }) {
        CodesDataBase.deleteCode<VerifyCodeDC>(e_mail)
    })
}