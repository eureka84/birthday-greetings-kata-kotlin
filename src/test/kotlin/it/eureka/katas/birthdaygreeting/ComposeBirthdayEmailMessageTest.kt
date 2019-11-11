package it.eureka.katas.birthdaygreeting

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test
import java.time.LocalDate

class ComposeBirthdayEmailMessageTest {

    @Test
    fun `build employee birthday message`() {
        val employee = Employee("Doe", "John", LocalDate.of(1980, 2, 29), EmailAddress("john.doe@email.com"))

        assertThat(composeMessage(employee)).isEqualTo(
            EmailMessage(
                to = EmailAddress("john.doe@email.com"),
                subject = "Birthday greetings",
                body = "Happy birthday John!"
            )
        )
    }
}