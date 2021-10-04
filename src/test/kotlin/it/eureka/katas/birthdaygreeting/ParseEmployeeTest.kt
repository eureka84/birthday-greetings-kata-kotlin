package it.eureka.katas.birthdaygreeting

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test
import java.time.LocalDate

class ParseEmployeeTest {

    @Test
    fun `read correct employee from CsvLine`() {
        assertThat(parseEmployee(CsvLine("Doe, John, 1982/10/08, john.doe@foobar.com")))
            .isEqualTo(
                Employee(
                    "Doe",
                    "John",
                    LocalDate.of(1982, 10, 8),
                    EmailAddress("john.doe@foobar.com")
                )
            )
    }

    @Test
    fun `malformed csv line`() {
        assertThat(parseEmployee(CsvLine("#comment")))
            .isEqualTo(
                ParseError(
                    source = "#comment",
                    cause = "java.lang.IndexOutOfBoundsException"
                )
            )
    }
}