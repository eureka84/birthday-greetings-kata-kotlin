package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

data class EmailMessage(val to: EmailAddress, val subject: String, val body: String)

fun sendBirthDayGreetingMail(
    composeMail: (Employee) -> EmailMessage,
    sendEmail: suspend (EmailMessage) -> Either<ProgramError, Unit>
): suspend (Employee) -> Either<ProgramError, Unit> = { employee -> sendEmail(composeMail(employee)) }

fun composeBirthdayEmailMessage(template: String): (Employee) -> EmailMessage = { e: Employee ->
    EmailMessage(
        to = e.emailAddress,
        subject = "Birthday greetings",
        body = template.format(e.firstName)
    )
}

fun sendEmail(conf: MailServerConfiguration): suspend (EmailMessage) -> Either<ProgramError, Unit> =
    { msg: EmailMessage ->
        Either.catch {
            val message = MimeMessage(conf.toSession())

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