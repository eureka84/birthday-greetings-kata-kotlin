package it.eureka.katas.birthdaygreeting

import assertk.assertThat
import assertk.assertions.isEqualTo
import it.msec.kio.failure
import it.msec.kio.just
import org.junit.Test
import java.time.LocalDate

class ParseEmployeeTest {

    @Test
    fun `read correct employee from CsvLine`() {
        assertThat(
            parseEmployee(CsvLine("Doe, John, 1982/10/08, john.doe@foobar.com"))
        ).isEqualTo(
            just(
                Employee(
                    "Doe",
                    "John",
                    LocalDate.of(1982, 10, 8),
                    EmailAddress("john.doe@foobar.com")
                )
            )
        )
    }

    @Test
    fun `malformed csv line`() {
        assertThat(
            parseEmployee(CsvLine("#comment"))
        ).isEqualTo(failure(ParseError("#comment")))
    }
}