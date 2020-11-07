package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import arrow.core.flatMap

typealias LoadEmployees = suspend (FileName) -> Either<ProgramError, List<Employee>>
typealias EmployeeFilter = (Employee) -> Boolean
typealias SendBirthdayGreetings = suspend (Employee) -> Either<ProgramError, Unit>
typealias SendGreetings = suspend (FileName) -> Either<ProgramError, Unit>

fun createSendGreetingsFunction(
    loadEmployees: LoadEmployees,
    employeeBornToday: EmployeeFilter,
    sendBirthDayGreetingMail: SendBirthdayGreetings
): SendGreetings = { sourceFile: FileName ->
    loadEmployees(sourceFile).flatMap { employees ->
        employees
            .filter(employeeBornToday)
            .map { e -> sendBirthDayGreetingMail(e) }
            .sequence().map { Unit }
    }
}