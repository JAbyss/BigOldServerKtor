package com.foggyskies.server.plugin

import com.foggyskies.server.databases.mongo.main.models.LocationByIPDC
import com.foggyskies.server.plugin.EmailSender.Title.accountWasLocked
import com.foggyskies.server.plugin.EmailSender.Title.codeConfirmation
import com.foggyskies.server.plugin.EmailSender.Title.signInAccount
import com.foggyskies.server.plugin.SystemRouting.Recovery.blockAccount
import com.foggyskies.server.plugin.SystemRouting.ipServer
import java.util.*
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailSender {

    const val emailCompany = "stingersword@gmail.com"
    const val emailCode = "nphdfatcqehigimb"

    private fun putIfMissing(props: Properties, key: String, value: String) {
        if (!props.containsKey(key)) {
            props[key] = value
        }
    }

    object Title {
        const val codeConfirmation = "Код подтверждения"
        const val signInAccount = "В ваш аккаунт выполнен вход"
        const val accountWasLocked = "Ваш аккаунт заблокирован"
    }

    private val property: Properties = run {
        val props = Properties()
        putIfMissing(props, "mail.smtp.host", "smtp.gmail.com")
        putIfMissing(props, "mail.smtp.port", "587")
        putIfMissing(props, "mail.smtp.auth", "true")
        putIfMissing(props, "mail.smtp.starttls.enable", "true")
        props
    }

    private val session: Session
        get() = run {

            Session.getDefaultInstance(property, object : javax.mail.Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(emailCompany, emailCode)
                }
            })
        }

    private fun generateMessage(
        address: String
    ): MimeMessage {

        val mimeMessage = MimeMessage(session)
        mimeMessage.setFrom(InternetAddress(emailCompany))
        mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address, false))
        mimeMessage.sentDate = Date()

        return mimeMessage
    }

    private fun MimeMessage.send() {
        val smtpTransport = session.getTransport("smtp")
        smtpTransport.connect()
        smtpTransport.sendMessage(this, this.allRecipients)
        smtpTransport.close()
    }

    fun sendCode(
        emailTo: String,
        code: String
    ) {

        val message = "\n   $code - код для подтверждения почты."

        try {
            val newMessage = generateMessage(emailTo)
            newMessage.setText(message)
            newMessage.subject = codeConfirmation


            newMessage.send()
        } catch (messagingException: MessagingException) {
            messagingException.printStackTrace()
        }
    }

    fun sendNotificationSignInOnAccount(
        location: LocationByIPDC,
        email: String,
        recoveryCode: String
    ) {
        val message =
            "В ваш акаунт выполнен вход с данного Ip: ${location.ip}\n" +
                    "Страна: ${location.country}\n" +
                    "Город: ${location.city} (${location.region})\n" +
                    "Провайдер: ${location.connection.org}\n" +
                    "Время входа: ${location.timezone.current_time}\n" +
                    "\n" +
                    "Если вход не санкционированный, вы можете заблокировать аккаунт по данной ссылке: ${ipServer + blockAccount + recoveryCode}" +
                    "\n" +
                    "С уважение администрация."

        try {
            val newMessage = generateMessage(email)
            newMessage.setText(message)
            newMessage.subject = signInAccount

            newMessage.send()
        } catch (messagingException: MessagingException) {
            messagingException.printStackTrace()
        }
    }

    fun sendBlockedNotification(
        email: String
    ){
        val message = """
            Ваш аккаунт успешно заблокирован.
            Востановить доступ к нему можно по следующей ссылке: 
        """.trimIndent()

        try {
            val newMessage = generateMessage(email)
            newMessage.setText(message)
            newMessage.subject = accountWasLocked

            newMessage.send()
        } catch (messagingException: MessagingException) {
            messagingException.printStackTrace()
        }
    }

    fun sendMail(
        emailTo: String,
        title: String,
        message: String
    ) {

        try {
            val mimeMessage = MimeMessage(session)
            mimeMessage.setFrom(InternetAddress(emailCompany))
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo, false))
            mimeMessage.setText(message)
            mimeMessage.subject = title
            mimeMessage.sentDate = Date()

            val smtpTransport = session.getTransport("smtp")
            smtpTransport.connect()
            smtpTransport.sendMessage(mimeMessage, mimeMessage.allRecipients)
            smtpTransport.close()
        } catch (messagingException: MessagingException) {
            messagingException.printStackTrace()
        }
    }
}