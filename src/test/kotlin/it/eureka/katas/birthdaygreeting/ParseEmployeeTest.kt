package it.eureka.katas.birthdaygreeting

import arrow.core.left
import arrow.core.right
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ParseEmployeeTest {

    @Test
    fun `read correct employee from CsvLine`() {
        assertThat(
            parseEmployee(CsvLine("Doe, John, 1982/10/08, john.doe@foobar.com")).unsafeRunSync()
        ).isEqualTo(
            Employee(
                "Doe",
                "John",
                LocalDate.of(1982, 10, 8),
                EmailAddress("john.doe@foobar.com")
            ).right()
        )
    }

    @Test
    fun `malformed csv line`() {
        assertThat(
            parseEmployee(CsvLine("#comment")).unsafeRunSync()
        ).isEqualTo(ParseError("#comment").left())
    }
}