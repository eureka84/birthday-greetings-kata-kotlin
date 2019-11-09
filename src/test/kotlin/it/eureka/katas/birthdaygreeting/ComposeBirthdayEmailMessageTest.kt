package it.eureka.katas.birthdaygreeting

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ComposeBirthdayEmailMessageTest {

    private val template = "Happy birthday %s!"
    private val composeMail = createComposeMessageFrom(template)

    @Test
    fun `build employee birthday message`() {
        val employee = Employee("Doe", "John", LocalDate.of(1980, 2, 29), EmailAddress("john.doe@email.com"))

        assertThat(composeMail(employee)).isEqualTo(
            EmailMessage(
                to = EmailAddress("john.doe@email.com"),
                subject = "Birthday greetings",
                body = "Happy birthday John!"
            )
        )
    }
}