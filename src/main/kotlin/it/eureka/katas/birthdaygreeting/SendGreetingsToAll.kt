package it.eureka.katas.birthdaygreeting

import arrow.core.computations.either

typealias SendGreetings = suspend (FileName) -> List<Result>

fun createSendGreetingsFunction(
    loadEmployees: LoadEmployees,
    employeeBornToday: EmployeeFilter,
    sendBirthDayGreetingMail: SendBirthdayGreetings
): SendGreetings = { sourceFile: FileName ->
    either<ProgramError, List<Result>> {
        val parseResults: List<ParseResult> = loadEmployees(sourceFile).bind()
        val (employees, parseErrors) = parseResults.partition { parseResult -> parseResult is Employee }
        val results: List<Result> =
            employees
                .map { it as Employee }
                .filter(employeeBornToday)
                .map { e -> sendBirthDayGreetingMail(e) }
        val overAllResults: List<Result> = results + parseErrors.map { (it as ParseError).toResult() }
        overAllResults.also { it.report() }
    }.fold(
        { listOf(it)},
        { it }
    )
}

private fun List<Result>.report() {
    this.forEach { r ->
        when(r) {
            is Sent -> println("Greetings sent to ${r.message.to}")
            is SendingError -> println("Failed to send greetings to ${r.emailMessage.to}")
            is ParseEmployeeError -> println("Failed to parse employee ${r.source}: ${r.cause}")
            is ReadFileError -> println("Failed to read file ${r.path}")
        }
    }
}
