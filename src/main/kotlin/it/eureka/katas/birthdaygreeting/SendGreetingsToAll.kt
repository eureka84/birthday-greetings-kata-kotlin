package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import arrow.core.flatMap

typealias SendGreetings = (FileName) -> Either<ProgramError, Unit>
typealias LoadEmployees = (FileName) -> Either<ProgramError, List<Employee>>
typealias EmployeeFilter = (Employee) -> Boolean
typealias SendBirthdayGreetings = (Employee) -> Either<ProgramError, Unit>

fun createSendGreetingsFunction(
    loadEmployees: LoadEmployees,
    employeeBornToday: EmployeeFilter,
    sendBirthDayGreetingMail: SendBirthdayGreetings
): SendGreetings = { sourceFile: FileName ->
    loadEmployees(sourceFile).flatMap { employees ->
        employees
            .filter(employeeBornToday)
            .map(sendBirthDayGreetingMail)
            .sequence().map { Unit }
    }
}