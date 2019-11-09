package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import arrow.core.flatMap
import kotlinx.coroutines.runBlocking

typealias SendGreetings = suspend (FileName) -> Either<ProgramError, Unit>
typealias LoadEmployees = suspend (FileName) -> Either<ProgramError, List<Employee>>
typealias EmployeeFilter = (Employee) -> Boolean
typealias SendBirthdayGreetings = suspend (Employee) -> Either<ProgramError, Unit>

fun createSendGreetingsFunction(
    loadEmployees: LoadEmployees,
    employeeBornToday: EmployeeFilter,
    sendBirthDayGreetingMail: SendBirthdayGreetings
): SendGreetings = { sourceFile: FileName ->
    loadEmployees(sourceFile).flatMap { employees ->
        employees
            .filter(employeeBornToday)
            .map { employee -> runBlocking { sendBirthDayGreetingMail(employee) } }
            .sequence().map { Unit }
    }
}