package it.eureka.katas.birthdaygreeting

import it.msec.kio.IO
import it.msec.kio.common.list.sequence
import it.msec.kio.flatMap
import it.msec.kio.map

typealias SendGreetings = (FileName) -> IO<ProgramError, Unit>
typealias LoadEmployees = (FileName) -> IO<ProgramError, List<Employee>>
typealias EmployeeFilter = (Employee) -> Boolean
typealias SendBirthdayGreetings = (Employee) -> IO<ProgramError, Unit>

fun createSendGreetingsFunction(
    loadEmployees: LoadEmployees,
    employeeBornToday: EmployeeFilter,
    sendBirthDayGreetingMail: SendBirthdayGreetings
): SendGreetings = { sourceFile: FileName ->
    loadEmployees(sourceFile).flatMap { employees ->
        employees
            .filter(employeeBornToday)
            .map { e -> sendBirthDayGreetingMail(e) }
            .sequence().map{ Unit }
    }
}