package com.foggyskies.server.routes

import com.foggyskies.server.data.model.ChatSession
import com.foggyskies.server.databases.message.models.FileDC
import com.foggyskies.server.extendfun.getSizeFile
import com.foggyskies.server.routes.cloud.fileTree
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import java.io.File
import java.util.*


fun Route.cloudRoute() {

    route("/cloud") {

        fileTree(false)

//        get("/allTree") {
//            val allFiles = File("cloud").listFiles()?.toList()?.map { file ->
//                FileDC(
//                    name = file.nameWithoutExtension,
//                    size = getSizeFile(file.length()),
//                    type = file.extension.let { return@let if (it == "") "dir" else it },
//                    path = file.path
//                )
//            }
//
//            call.respond(HttpStatusCode.OK, allFiles ?: emptyList())
//
//        }

        webSocket("/downloadCloud") {

            val session = call.sessions.get<ChatSession>()
            if (session == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
                return@webSocket
            }

            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {

                    val command = frame.readText()

                    /**
                     *  Command
                     *  1: address to file
                     */

                    val selectedFile = File(command)

                    selectedFile.inputStream().use { input ->
                        var arr =
                            if (selectedFile.length() / 8 < 4096) ByteArray((selectedFile.length() / 8).toInt()) else ByteArray(4096000)
                        var allReaded = 0L
                        val maxSize = selectedFile.length()
                        do {
                            val size =
                                if (maxSize - allReaded < arr.size) {
                                    println("Check ${maxSize - allReaded}")
                                    arr = ByteArray((maxSize - allReaded).toInt())
//                    println(a.size)
                                    input.read(arr)
                                } else
                                    input.read(arr)
                            if (size <= 0) {
//                    socket.send(Frame.Text("|>finish<|>$nameOperation<|"))
                                println(allReaded)
                                break
                            } else {
                                allReaded += size
                                val base64 = Base64.getEncoder().encodeToString(arr)
                                println("Я чето делаю")
                                this.send(Frame.Text("1234.${selectedFile.extension}|$base64"))
                            }
                        } while (true)
                    }


                }
            }


        }

        get("/abc") {
            call.respondFile(File("images/Arcane. Season 01. Episode 01. By Wild_Cat.mkv"))
        }
    }

}