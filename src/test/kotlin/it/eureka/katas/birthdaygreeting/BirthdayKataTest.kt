package it.eureka.katas.birthdaygreeting

import assertk.assertThat
import assertk.assertions.containsAll
import assertk.assertions.isEmpty
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.subethamail.smtp.server.SMTPServer
import java.time.LocalDate

const val MAIL_SERVER_PORT = 1025

class BirthdayKataTest {

    private val sentMessageListener = TestMessageListener()
    private val today = LocalDate.of(2019, 9, 11)
    private val server = SMTPServer.port(MAIL_SERVER_PORT).messageHandler(sentMessageListener).build()

    private val sendBirthDayGreetingMail = createSendBirthDayGreetingMail(
        composeMessage,
        createSendEmailFrom(
            MailServerConfiguration(
                host = "localhost",
                port = MAIL_SERVER_PORT
            )
        )
    )
    private val loadEmployees = loadEmployees(::readCsv, ::parseEmployee)
    private val employeeBornToday = createEmployeeBirthdayFilterFor(today)

    private val sendGreetingsToAll: SendGreetings = createSendGreetingsFunction(
        loadEmployees,
        employeeBornToday,
        sendBirthDayGreetingMail
    )

    @BeforeEach
    fun setUp() {
        server.start()
    }

    @AfterEach
    fun tearDown() {
        server.stop()
    }

    @Test
    fun `happy path`() {
        runBlocking {
            sendGreetingsToAll(FileName("/fixtures/bigFile.csv"))
        }
        assertThat(sentMessageListener.recipients).containsAll(
            "mary.ann@foobar.com",
            "caty.ann@foobar.com",
            "maggie.ann@foobar.com",
            "sue.ann@foobar.com",
            "daisy.ann@foobar.com",
            "lucy.ann@foobar.com"
        )
    }

    @Test
    fun `csv file with errors`() {
        runBlocking {
            sendGreetingsToAll(FileName("/fixtures/wrongFile.csv"))
        }
        assertThat(sentMessageListener.recipients).isEmpty()
    }

}
