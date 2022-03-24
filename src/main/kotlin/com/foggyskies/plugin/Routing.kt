package com.foggyskies.plugin

import com.foggyskies.chat.routes.*
import io.ktor.application.*
import io.ktor.routing.*

fun Application.configureRouting() {
//    val roomUserController by inject<UserRoomController>()

    install(Routing) {
        usersRoutes()
        authRoutes()
        createChatRoutes()
        chatSessionRoutes()
        notificationRoutes()
//        chatListRoutes()
        photoRouting()
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