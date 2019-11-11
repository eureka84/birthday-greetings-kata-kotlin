package it.eureka.katas.birthdaygreeting

import arrow.core.left
import arrow.core.right
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test

class ReadCsvTest {

    @Test
    fun `non existing file`() {
        assertThat(
            readCsv(FileName("/fixtures/a_non_existing_file")).unsafeRunSync()
        ).isEqualTo(ReadFileError("/fixtures/a_non_existing_file").left())
    }

    @Test
    fun `empty file so empty csv`() {
        assertThat(
            readCsv(FileName("/fixtures/emptyFile.csv")).unsafeRunSync()
        ).isEqualTo(CsvFile(listOf()).right())
    }

    @Test
    internal fun `filled csv`() {
        assertThat(
            readCsv(FileName("/fixtures/goodFile.csv")).unsafeRunSync()
        ).isEqualTo(
            CsvFile(
                listOf(
                    CsvLine("Doe, John, 1982/10/08, john.doe@foobar.com"),
                    CsvLine("Ann, Mary, 1975/09/11, mary.ann@foobar.com")
                )
            ).right()
        )
    }
}