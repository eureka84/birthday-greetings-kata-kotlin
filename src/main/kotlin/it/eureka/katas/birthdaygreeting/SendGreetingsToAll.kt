package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import arrow.fx.IO
import arrow.fx.fix
import arrow.mtl.EitherT

typealias SendGreetings = (FileName) -> IO<Either<ProgramError, Unit>>
typealias LoadEmployees = (FileName) -> IO<Either<ProgramError, List<Employee>>>
typealias EmployeeFilter = (Employee) -> Boolean
typealias SendBirthdayGreetings = (Employee) -> IO<Either<ProgramError, Unit>>

fun createSendGreetingsFunction(
    loadEmployees: LoadEmployees,
    employeeBornToday: EmployeeFilter,
    sendBirthDayGreetingMail: SendBirthdayGreetings
): SendGreetings = { sourceFile: FileName ->
    EitherT(loadEmployees(sourceFile)).flatMap { employees ->
        employees
            .filter(employeeBornToday)
            .map { e -> EitherT(sendBirthDayGreetingMail(e)) }
            .sequence().map { Unit }
    }.value().fix()
}