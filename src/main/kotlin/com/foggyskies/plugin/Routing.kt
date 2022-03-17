package com.foggyskies.plugin

import com.foggyskies.chat.routes.*
import com.foggyskies.chat.room.AuthRoomController
import com.foggyskies.chat.room.UserRoomController
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import java.io.File

fun Application.configureRouting() {
    val roomAuthController by inject<AuthRoomController>()
    val roomUserController by inject<UserRoomController>()

    install(Routing) {
        chatSocket()
        usersRoutes(roomUserController)
        authRoutes(roomAuthController)
        createChatRoutes()
        subscribeRoutes()
        chatListRoutes()
        photoRouting()
    }
}

fun Route.photoRouting() {

    get("/photo{name}") {
        val name = call.parameters["name"]
        val file = File("photos/$name")
        call.response.header(
            HttpHeaders.ContentDisposition,
            ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, name!!)
                .toString()
        )
        call.respondFile(file)
    }
}

//fun encrypt(str: String): String {
//    var encryptString = ""
//
//    val en_list = mapOf(
//        'a' to "Wsfh",
//        'b' to "Dajhf",
//        'c' to "@5fc",
//        'd' to "*g@fa",
//        'e' to "lmgi",
//        'f' to "uinTkf",
//        'g' to "nFoawg",
//        'h' to "FWs^Awf#",
//        'i' to "fwf@#",
//        'j' to "lafh",
//        'k' to "Cvbd##",
//        'l' to "#fahs",
//        'm' to "ffg#$5",
//        'n' to "fwWmlbp",
//        'o' to "klvk",
//        'p' to "nmutb",
//        'q' to "nbvcYOP",
//        'r' to "UCbsktp",
//        's' to "Ncxf",
//        't' to "pozxmr",
//        'u' to "ghJks",
//        'v' to "kafwSf$@",
//        'w' to "ksI&@",
//        'x' to "lkm@!",
//        'y' to "*xs@4",
//        'z' to "sg$2k"
//    )
//    val en_list_big = mapOf(
//        'A' to "\$fsfh",
//        'B' to "Dgdjhf",
//        'C' to "@dwac",
//        'D' to "*g@htha",
//        'E' to "lmg@#",
//        'F' to "ui!!tdkf",
//        'G' to "nFFS@@wg",
//        'H' to "F^Awf#",
//        'I' to "hwf@#",
//        'J' to "lafh",
//        'K' to "Cvfad##",
//        'L' to "#fBVrs",
//        'M' to "fFWF#$5",
//        'N' to "fw%lbp",
//        'O' to "kl&*k",
//        'P' to "nmutFAFb",
//        'Q' to "nbvcFWFW%P",
//        'R' to "UCFASxsktp",
//        'S' to "Gsb@!",
//        'T' to "D!@E@",
//        'U' to "ccsaS",
//        'V' to "FWBvey@",
//        'W' to "ASv&@",
//        'X' to "lkStm@!",
//        'Y' to "*xAs@4",
//        'Z' to "sxVg$2k"
//    )
//
//    repeat(2) {
//        str.forEach { char ->
//            encryptString +=
//                if (char.isUpperCase() && char.isLetter())
//                    en_list_big[char]
//                else if (char.isLetter())
//                    en_list[char]
//                else
//                    ""
//        }
//    }
//    return encryptString
//}