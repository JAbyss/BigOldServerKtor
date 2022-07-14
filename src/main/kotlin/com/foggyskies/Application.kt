package com.foggyskies

//import com.foggyskies.chat.extendfun.generateUUID
//import com.foggyskies.plugin.configureRouting
//import com.foggyskies.plugin.configureSecurity
//import com.foggyskies.plugin.configureSockets
//import com.foggyskies.plugin.mainModule
//import io.ktor.http.*
//import io.ktor.serialization.kotlinx.json.*
//import io.ktor.server.application.*
////import io.ktor.server.application.*
//import io.ktor.server.plugins.contentnegotiation.*
//import io.ktor.server.plugins.forwardedheaders.*
//import kotlinx.serialization.json.Json
//import org.koin.ktor.plugin.Koin
//import java.text.SimpleDateFormat
//import java.util.*
//import javax.mail.Message
//import javax.mail.MessagingException
//import javax.mail.PasswordAuthentication
//import javax.mail.Session
//import javax.mail.internet.InternetAddress
//import javax.mail.internet.MimeMessage

import com.foggyskies.plugin.configureRouting
import com.foggyskies.plugin.configureSecurity
import com.foggyskies.plugin.configureSockets
import com.foggyskies.plugin.mainModule
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*
import org.koin.ktor.ext.Koin
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.json
import org.litote.kmongo.reactivestreams.KMongo

//fun main() {
//
//    Json.encodeToString(FormattedItem<Char>(item = 'A', isVisible = false))
//
//    fun <T> List<T>.transformToFormattedItem(): MutableList<FormattedItem<T>> {
//        val newList = mutableListOf<FormattedItem<T>>()
//        this.forEach { item ->
//            val newItem = FormattedItem(
//                item = item,
//                isVisible = false
//            )
//            newList.add(newItem)
//        }
//        return newList
//    }
//
//    fun <T> MutableList<T>.processingList(work: OldListInfo<T>){
//            work.newItemsList.forEach {
//                this.add(it)
//            }
//    }
//
//    fun firstInit() {
//        newList = mutableListOf('A', 'B', 'C', 'D')
//        oldList = newList
//        transformedList = newList.transformToFormattedItem()
//        println("newList - $newList\n oldLsit - $oldList \n transformedList - $transformedList")
//    }
//
//    fun secondStage(){
//        newList = mutableListOf('A', 'V', 'V', 'D')
//        checkOldListByNewList(oldList, newList)
//    }
//
//
//
//    val oldList = mutableListOf<Char>('А', 'Г', 'Е', 'Д', 'F', 'L', 'Y', 'X')
//    val newList = mutableListOf<Char>('А', 'Е', 'Ж', 'М', 'X', 'V')
//
//    val result = checkOldListByNewList(oldList, newList)
//
//    println(result)
//
//}

enum class DataBases {
    MAIN, MESSAGES, SUBSCRIBERS, CONTENT, NEW_MESSAGE
}

object ServerDate {
    private val formatFull = SimpleDateFormat("d MMM yyyy г. HH:mm:ss")
    private val formatMute = SimpleDateFormat("ddhhmm")

    val fullDate: String
        get() =
            formatFull.format(Date())

    val muteDate: String
        get() = formatMute.format(Date())
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {

    install(Koin) {
        modules(mainModule)
    }
//    install(Forward)
//    install(ForwardedHeaderSupport)
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
//    install(ForwardedHeaders)
//    install(XForwardedHeaders)
    configureSockets()
    configureRouting()
    configureSecurity()
}
//suspend fun main() {
//
//    val a = KMongo.createClient("mongodb://localhost:27018/?directConnection=true")
//        .coroutine
//        .getDatabase("petapp_db")
//    println(a.getCollection<UserMainEntity>().find().toList())
//
//}


//fun main(args: Array<String>) {
//    val userName =  "stingersword@gmail.com"
//    val password =  "uvnatdpxaocmgdux"
//    // FYI: passwords as a command arguments isn't safe
//    // They go into your bash/zsh history and are visible when running ps
//
//    val emailFrom = "stingersword@gmail.com"
//    val emailTo = "f.tiratore.k@gmail.com"
////    val emailCC = "fawf@mail.ru"
//    val code = generateUUID(4)
////    val subject = "Код подтверждения"
////    val text = "\n$code - код для подтверждения почты."
//
//    val subject = "Jarvis"
//    val text = "Jarvis не дремлит."
//
//    val props = Properties()
//    putIfMissing(props, "mail.smtp.host", "smtp.gmail.com")
//    putIfMissing(props, "mail.smtp.port", "587")
//    putIfMissing(props, "mail.smtp.auth", "true")
//    putIfMissing(props, "mail.smtp.starttls.enable", "true")
//
//    val session = Session.getDefaultInstance(props, object : javax.mail.Authenticator() {
//        override fun getPasswordAuthentication(): PasswordAuthentication {
//            return PasswordAuthentication(userName, password)
//        }
//    })
//
//    session.debug = true
//
//    try {
//        val mimeMessage = MimeMessage(session)
//        mimeMessage.setFrom(InternetAddress(emailFrom))
//        mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo, false))
////        mimeMessage.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailCC, false))
//        mimeMessage.setText(text)
//        mimeMessage.subject = subject
//        mimeMessage.sentDate = Date()
//
//        val smtpTransport = session.getTransport("smtp")
//        smtpTransport.connect()
//            smtpTransport.sendMessage(mimeMessage, mimeMessage.allRecipients)
//        smtpTransport.close()
//    } catch (messagingException: MessagingException) {
//        messagingException.printStackTrace()
//    }
//}
//
//private fun putIfMissing(props: Properties, key: String, value: String) {
//    if (!props.containsKey(key)) {
//        props[key] = value
//    }
//}