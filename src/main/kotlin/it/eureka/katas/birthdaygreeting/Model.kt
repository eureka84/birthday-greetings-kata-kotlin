package it.eureka.katas.birthdaygreeting

import java.time.LocalDate

data class Employee(
    val lastName: String,
    val firstName: String,
    val birthDate: LocalDate,
    val emailAddress: EmailAddress
)

@JvmInline
value class EmailAddress(val value: String)

sealed class ProgramError
data class ReadFileError(val path: String) : ProgramError()
data class ParseError(val source: String) : ProgramError()
data class MailSendingError(val emailMessage: EmailMessage) : ProgramError()