package com.foggyskies.server.routes.auth.requests

import com.foggyskies.ServerDate
import com.foggyskies.server.databases.mongo.codes.models.InfoLockDC
import com.foggyskies.server.databases.mongo.codes.models.LockCodeDC
import com.foggyskies.server.databases.mongo.testpacage.codes.CodesDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.lockUser
import com.foggyskies.server.databases.mongo.main.models.BlockUserDC
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.blockAccount() = cRoute(
    SystemRouting.AuthRoute.blockAccount,
    HttpMethod.Get,
    isCheckToken = false
) {
    val code = call.request.queryParameters["code"] ?: return@cRoute call.respond(
        status = HttpStatusCode.BadRequest,
        "Код не получен"
    )

    checkCode(code) { blockUser(it) } ?: return@cRoute call.respond(HttpStatusCode.NotFound, "Код не найден.")

    call.respond(HttpStatusCode.OK, "Успешно заблокирован")
}

private suspend inline fun checkCode(idCode: String, existAction: (LockCodeDC) -> Unit): Unit? {
    val code = com.foggyskies.server.databases.mongo.testpacage.codes.CodesDataBase.getCodeByID<LockCodeDC>(idCode) ?: return null
    return existAction(code)
}

private suspend inline fun blockUser(lockCode: LockCodeDC) {

    val infoLock = com.foggyskies.server.databases.mongo.testpacage.codes.CodesDataBase.getCodeByID<InfoLockDC>(lockCode.lock_code)!!
    val block = BlockUserDC(
        lock_code = infoLock.id,
        time_lock = ServerDate.fullDate,
        time_unlock = infoLock.time_to_block
    )
    com.foggyskies.server.databases.mongo.testpacage.codes.CodesDataBase.insertCodes(block)
    MainDataBase.Users.lockUser(lockCode.idUser, true)
    com.foggyskies.server.databases.mongo.testpacage.codes.CodesDataBase.deleteCode<LockCodeDC>(lockCode.id)
}