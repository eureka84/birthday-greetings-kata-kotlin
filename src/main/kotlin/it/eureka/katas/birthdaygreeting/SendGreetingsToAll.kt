package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import arrow.core.flatMap
import kotlinx.coroutines.runBlocking

fun sendGreetings(
    loadEmployees: suspend (FileName) -> Either<ProgramError, List<Employee>>,
    employeeBornToday: (Employee) -> Boolean,
    sendBirthDayGreetingMail: suspend (Employee) -> Either<ProgramError, Unit>
): suspend (FileName) -> Either<ProgramError, Unit> = { sourceFile: FileName ->
    loadEmployees(sourceFile).flatMap { employees ->
        employees
            .filter(employeeBornToday)
            .map { employee -> runBlocking { sendBirthDayGreetingMail(employee) } }
            .sequence().map { Unit }
    }
}