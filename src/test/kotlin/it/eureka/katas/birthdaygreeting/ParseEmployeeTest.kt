package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.fx.coroutines.Environment
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test
import java.time.LocalDate

class ParseEmployeeTest {

    @Test
    fun `read correct employee from CsvLine`() {
        Environment().unsafeRunSync {
            val actual: Either<ProgramError, Employee> =
                parseEmployee(CsvLine("Doe, John, 1982/10/08, john.doe@foobar.com"))

            assertThat(actual).isEqualTo(
                Employee(
                    "Doe",
                    "John",
                    LocalDate.of(1982, 10, 8),
                    EmailAddress("john.doe@foobar.com")
                ).right()
            )
        }
    }

    @Test
    fun `malformed csv line`() {
        Environment().unsafeRunSync {
            val actual = parseEmployee(CsvLine("#comment"))

            assertThat(actual).isEqualTo(ParseError("#comment").left())
        }
    }
}