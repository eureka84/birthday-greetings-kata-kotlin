package it.eureka.katas.birthdaygreeting

fun sendGreetings(
    loadEmployees: LoadEmployees,
    employeeBornToday: EmployeePredicate,
    sendBirthDayGreetingMail: SendBirthdayGreetingMail
): (FileName) -> IOEither<ProgramError, Unit> = { sourceFile: FileName ->
    loadEmployees(sourceFile).flatMap { employees ->
        employees
            .filter(employeeBornToday)
            .map(sendBirthDayGreetingMail)
            .sequence().map { Unit }
    }
}