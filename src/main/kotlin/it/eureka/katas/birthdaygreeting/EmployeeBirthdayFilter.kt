package it.eureka.katas.birthdaygreeting

import java.time.LocalDate
import java.time.Month
import java.time.Month.FEBRUARY

fun createEmployeeBirthdayFilterFor(today: LocalDate): EmployeeFilter = { e: Employee ->
    when {
        e.isBornOn(FEBRUARY, 29) && today.isLeapYear -> today.`is`(FEBRUARY, 29)
        e.isBornOn(FEBRUARY, 29) -> today.`is`(FEBRUARY, 28)
        else -> today isSameDateAs e.birthDate
    }
}

private fun LocalDate.`is`(month: Month, dayOfMonth: Int): Boolean =
    this.month == month && this.dayOfMonth == dayOfMonth

private fun Employee.isBornOn(month: Month, dayOfMonth: Int) = this.birthDate.`is`(month, dayOfMonth)

private infix fun LocalDate.isSameDateAs(other: LocalDate) =
    other.month == this.month && other.dayOfMonth == this.dayOfMonth
