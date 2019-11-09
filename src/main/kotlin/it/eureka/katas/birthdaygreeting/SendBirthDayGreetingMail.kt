package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

data class EmailMessage(val to: EmailAddress, val subject: String, val body: String)

typealias SendBirthdayGreetingMail = suspend (Employee) -> Either<ProgramError, Unit>
typealias ComposeMessage = (Employee) -> EmailMessage
typealias SendEmail = suspend (EmailMessage) -> Either<ProgramError, Unit>

fun sendBirthDayGreetingMail(
    composeMail: ComposeMessage,
    sendEmail: SendEmail
): SendBirthdayGreetingMail = { employee -> sendEmail(composeMail(employee)) }

fun composeBirthdayEmailMessage(template: String): ComposeMessage = { e: Employee ->
    EmailMessage(
        to = e.emailAddress,
        subject = "Birthday greetings",
        body = template.format(e.firstName)
    )
}

suspend fun sendEmail(mailServerConfiguration: MailServerConfiguration): SendEmail = { msg: EmailMessage ->
    Either.catch {
        val message = MimeMessage(mailServerConfiguration.toSession())

        message.setFrom(InternetAddress("no-reply@myservice.com"))
        message.addRecipient(Message.RecipientType.TO, InternetAddress(msg.to.value))
        message.subject = msg.subject
        message.setText(msg.body)

        Transport.send(message)
    }.mapLeft { MailSendingError(msg) as ProgramError }

}

data class MailServerConfiguration(val host: String, val port: Int) {
    fun toSession(): Session {
        val properties = Properties()
        properties.setProperty("mail.smtp.host", host)
        properties.setProperty("mail.smtp.port", port.toString())

        return Session.getDefaultInstance(properties)
    }
}