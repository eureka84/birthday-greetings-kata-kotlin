package it.eureka.katas.birthdaygreeting

import assertk.assertThat
import assertk.assertions.isEqualTo
import it.msec.kio.result.Failure
import it.msec.kio.result.Success
import it.msec.kio.runtime.Runtime.unsafeRunSync
import org.junit.Test
import java.time.LocalDate

class ParseEmployeeTest {

    @Test
    fun `read correct employee from CsvLine`() {
        assertThat(
            unsafeRunSync(parseEmployee(CsvLine("Doe, John, 1982/10/08, john.doe@foobar.com")))
        ).isEqualTo(
            Success(
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
            unsafeRunSync(parseEmployee(CsvLine("#comment")))
        ).isEqualTo(Failure(ParseError("#comment")))
    }
}