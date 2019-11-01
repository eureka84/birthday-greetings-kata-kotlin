package it.eureka.katas.birthdaygreeting

fun sendGreetingsToAll(
    loadEmployees: (FileName) -> IOEither<ProgramError, List<Employee>>,
    employeeBorneToday: (Employee) -> Boolean,
    sendBirthDayGreetingMail: (Employee) -> IOEither<ProgramError, Unit>
): (FileName) -> IOEither<ProgramError, Unit> = { sourceFile: FileName ->
    loadEmployees(sourceFile).flatMap { employees ->
        employees
            .filter(employeeBorneToday)
            .map(sendBirthDayGreetingMail)
            .sequence().map { Unit }
    }
}