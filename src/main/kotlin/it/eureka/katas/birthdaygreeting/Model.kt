package it.eureka.katas.birthdaygreeting

import java.time.LocalDate

@JvmInline
value class EmailAddress(val value: String)

sealed class ParseResult
data class Employee(
    val lastName: String,
    val firstName: String,
    val birthDate: LocalDate,
    val emailAddress: EmailAddress
): ParseResult()
data class ParseError(val source: String, val cause: String = "") : ParseResult() {
    fun toResult(): ParseEmployeeError = ParseEmployeeError(source, cause)
}

sealed class Result
sealed class ProgramError: Result()
data class ReadFileError(val path: String) : ProgramError()
data class ParseEmployeeError(val source: String, val cause: String = "") : ProgramError()

sealed class GreetingMail: Result()
data class Sent(val message: EmailMessage): GreetingMail()
data class SendingError(val emailMessage: EmailMessage) : GreetingMail()