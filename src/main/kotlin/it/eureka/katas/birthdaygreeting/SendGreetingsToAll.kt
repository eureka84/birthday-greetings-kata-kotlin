package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import arrow.fx.IO
import arrow.fx.extensions.io.functor.functor
import arrow.fx.extensions.io.monad.monad
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
    EitherT(loadEmployees(sourceFile)).flatMap(IO.monad()) { employees ->
        employees
            .filter(employeeBornToday)
            .map { e -> EitherT(sendBirthDayGreetingMail(e)) }
            .sequence().map(IO.functor()) { Unit }
    }.value().fix()
}