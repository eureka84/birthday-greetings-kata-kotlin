package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import arrow.core.computations.either
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

typealias LoadEmployees = suspend (FileName) -> Either<ProgramError, List<Employee>>
typealias EmployeeFilter = (Employee) -> Boolean
typealias SendBirthdayGreetings = suspend (Employee) -> Either<ProgramError, Unit>
typealias SendGreetings = suspend (FileName) -> Either<ProgramError, Unit>

fun createSendGreetingsFunction(
    loadEmployees: LoadEmployees,
    employeeBornToday: EmployeeFilter,
    sendBirthDayGreetingMail: SendBirthdayGreetings
): SendGreetings = { sourceFile: FileName ->
    either {
        val employees: List<Employee> = loadEmployees(sourceFile).bind()
        val results: List<Either<ProgramError, Unit>> =
            employees
                .filter(employeeBornToday)
                .map { e -> sendBirthDayGreetingMail(e) }
       results.report()
    }
}

private suspend fun List<Either<ProgramError, Unit>>.report() {
    val self = this
    coroutineScope {
        val list: List<Either.Left<ProgramError>> =
            self.filter { it.isLeft() }
                .map { it as Either.Left<ProgramError> }
        list.forEach { error -> launch { println(error.a) } }
    }
}
