package it.eureka.katas.birthdaygreeting

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class EmployeeBirthdayFilterTest {

    @Test
    fun `simple positive case`() {
        val today = LocalDate.of(2019, 7, 6)
        val employee = Employee(
            "Doe",
            "John",
            LocalDate.of(1970, 7, 6),
            EmailAddress("john@email.com")
        )

        assertTrue(createEmployeeBirthdayFilterFor(today)(employee))
    }

    @Test
    fun `simple negative case`() {
        val today = LocalDate.of(2019, 7, 6)
        val employee = Employee(
            "Doe",
            "John",
            LocalDate.of(1970, 7, 7),
            EmailAddress("john@email.com")
        )

        assertFalse(createEmployeeBirthdayFilterFor(today)(employee))
    }


    @Test
    fun `employee born on 29th February and today is the 29th of February`() {
        val today = LocalDate.of(2020, 2, 29)
        val employee = Employee(
            "Doe",
            "John",
            LocalDate.of(1980, 2, 29),
            EmailAddress("john@email.com")
        )

        assertTrue(createEmployeeBirthdayFilterFor(today)(employee))
    }

    @Test
    fun `employee born on 29th February and today is the 28th of February of a non leap year`() {
        val today = LocalDate.of(2019, 2, 28)
        val employee = Employee(
            "Doe",
            "John",
            LocalDate.of(1980, 2, 29),
            EmailAddress("john@email.com")
        )

        assertTrue(createEmployeeBirthdayFilterFor(today)(employee))
    }

    @Test
    fun `employee born on 29th February and today is the 28th of February of leap year`() {
        val today = LocalDate.of(2020, 2, 28)
        val employee = Employee(
            "Doe",
            "John",
            LocalDate.of(1980, 2, 29),
            EmailAddress("john@email.com")
        )

        assertFalse(createEmployeeBirthdayFilterFor(today)(employee))
    }

    @Test
    fun `employee born on 29th February and today is not his birthday`() {
        val today = LocalDate.of(2020, 1, 1)
        val employee = Employee(
            "Doe",
            "John",
            LocalDate.of(1980, 2, 29),
            EmailAddress("john@email.com")
        )

        assertFalse(createEmployeeBirthdayFilterFor(today)(employee))
    }

}