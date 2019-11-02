package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import arrow.core.andThen
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

data class EmailMessage(val to: EmailAddress, val subject: String, val body: String)

typealias SendBirthdayGreetingMail = (Employee) -> IOEither<ProgramError, Unit>
typealias ComposeMessage = (Employee) -> EmailMessage
typealias SendEmail = (EmailMessage) -> IOEither<ProgramError, Unit>

fun sendBirthDayGreetingMail(composeMail: ComposeMessage, sendEmail: SendEmail): SendBirthdayGreetingMail =
     (composeMail andThen sendEmail)

fun composeBirthdayEmailMessage(template: String): ComposeMessage = { e: Employee ->
    EmailMessage(
        to = e.emailAddress,
        subject = "Birthday greetings",
        body = template.format(e.firstName)
    )
}

fun sendEmail(mailServerConfiguration: MailServerConfiguration): SendEmail = { msg: EmailMessage ->
    runBlocking {
        IOEitherFrom(
            Either.catch {
                val message = MimeMessage(mailServerConfiguration.toSession())

                message.setFrom(InternetAddress("no-reply@myservice.com"))
                message.addRecipient(Message.RecipientType.TO, InternetAddress(msg.to.value))
                message.subject = msg.subject
                message.setText(msg.body)

                Transport.send(message)
            }.mapLeft { MailSendingError(msg) as ProgramError }
        )
    }
}

data class MailServerConfiguration(val host: String, val port: Int) {
    fun toSession(): Session {
        val properties = Properties()
        properties.setProperty("mail.smtp.host", host)
        properties.setProperty("mail.smtp.port", port.toString())

        return Session.getDefaultInstance(properties)
    }
}