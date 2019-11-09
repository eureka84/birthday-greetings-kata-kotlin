package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import arrow.core.flatMap
import kotlinx.coroutines.runBlocking

fun sendGreetings(
    loadEmployees: LoadEmployees,
    employeeBornToday: EmployeePredicate,
    sendBirthDayGreetingMail: SendBirthdayGreetingMail
): suspend (FileName) -> Either<ProgramError, Unit> = { sourceFile: FileName ->
    loadEmployees(sourceFile).flatMap { employees ->
        employees
            .filter(employeeBornToday)
            .map { employee -> runBlocking { sendBirthDayGreetingMail(employee) } }
            .sequence().map { Unit }
    }
}