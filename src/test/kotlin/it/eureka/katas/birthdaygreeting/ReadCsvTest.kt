package it.eureka.katas.birthdaygreeting

import assertk.assertThat
import assertk.assertions.isEqualTo
import it.msec.kio.failure
import it.msec.kio.just
import org.junit.Test

class ReadCsvTest {

    @Test
    fun `non existing file`() {
        assertThat(
            readCsv(FileName("/fixtures/a_non_existing_file"))
        ).isEqualTo(
            failure(ReadFileError("/fixtures/a_non_existing_file"))
        )
    }

    @Test
    fun `empty file so empty csv`() {
        assertThat(
            readCsv(FileName("/fixtures/emptyFile.csv"))
        ).isEqualTo(
            just(CsvFile(listOf()))
        )
    }

    @Test
    internal fun `filled csv`() {
        assertThat(
            readCsv(FileName("/fixtures/goodFile.csv"))
        ).isEqualTo(
            just(
                CsvFile(
                    listOf(
                        CsvLine("Doe, John, 1982/10/08, john.doe@foobar.com"),
                        CsvLine("Ann, Mary, 1975/09/11, mary.ann@foobar.com")
                    )
                )
            )
        )
    }
}