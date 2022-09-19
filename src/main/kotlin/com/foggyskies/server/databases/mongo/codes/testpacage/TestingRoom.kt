package com.foggyskies.server.databases.mongo.codes.testpacage

import com.foggyskies.ServerDate
import com.foggyskies.server.databases.mongo.codes.testpacage.settings.SystemSettingsDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.settings.insertLog
import com.foggyskies.server.databases.mongo.main.models.Token
import io.ktor.http.*
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object Logger {

    //    @kotlinx.serialization.Serializable
    data class LoggBody(
        val method: String,
        val path: String,
        val isCheckToken: Boolean,
        val ipAddress: String,
        val token: Token,
        val errorLogs: MutableList<LoggDc> = mutableListOf(),
        val infoLogs: MutableList<LoggDc> = mutableListOf()
    )

    //    @kotlinx.serialization.Serializable
    data class LoggDc(
        val time: String,
        val message: Any,
        val status: StatusCodes,
    )

    val logs = ConcurrentHashMap<String, LoggBody>()

    enum class StatusCodes {
        INFO, ERROR
    }

    fun initLog(
        idRequest: String,
        method: String,
        path: String,
        isCheckToken: Boolean,
        ipAddress: String,
        token: Token
    ) {

        val value = LoggBody(
            method,
            path,
            isCheckToken,
            ipAddress,
            token
        )

        logs[idRequest] = value
    }

    fun addLog(idRequest: String, message: Any, status: StatusCodes) {

        val messageLog = LoggDc(
            time = ServerDate.fullDate,
            message,
            status
        )
        if (messageLog.status == StatusCodes.ERROR)
            logs.errorPut(idRequest, messageLog)
        else
            logs.infoPut(idRequest, messageLog)
    }

    suspend fun saveLog(idLog: String) {
        SystemSettingsDataBase.Logs.insertLog(logs[idLog]!!)
        logs.remove(idLog)
    }

    private fun <K : Any> ConcurrentHashMap<K, LoggBody>.errorPut(key: K, value: LoggDc) {
        this[key]?.errorLogs?.add(value)
    }

    private fun <K : Any> ConcurrentHashMap<K, LoggBody>.infoPut(key: K, value: LoggDc) {
        this[key]?.infoLogs?.add(value)
    }

}

//fun Route.deleteMessage(isCheckToken: Boolean) = cRoute(
//    path = SystemRouting.ChatRoute.deleteMessage,
//    method = HttpMethod.Post,
//    isCheckToken
//) {
//
//    val deleteMessageEntity = call.receive<DeleteMessageEntity>()
//
//    val fullMessage =
//        MessagesDataBase.Messages.getMessageById(deleteMessageEntity.idChat, deleteMessageEntity.idMessage)
//            ?: NewMessagesDataBase.NewMessages.getMessageById(
//                idUser = deleteMessageEntity.idUser,
//                idChat = deleteMessageEntity.idChat,
//                idMessage = deleteMessageEntity.idMessage
//            )
//
//
//    fullMessage.listFiles.forEach { File(it.path).delete() }
//    fullMessage.listImages.forEach { File(it).delete() }
//
//    val code = deleteMessage(deleteMessageEntity)
//
//    call.respondText(status = HttpStatusCode.OK, text = code.toString())
//}
//
//private suspend fun deleteMessage(deleteMessageDC: DeleteMessageEntity): Int {
//    return if (MessagesDataBase.Messages.deleteMessage(deleteMessageDC.idChat, deleteMessageDC.idMessage) == 0)
//        NewMessagesDataBase.NewMessages.deleteNewMessage(
//            deleteMessageDC.idUser,
//            deleteMessageDC.idChat,
//            deleteMessageDC.idMessage
//        )
//    else
//        1
//}