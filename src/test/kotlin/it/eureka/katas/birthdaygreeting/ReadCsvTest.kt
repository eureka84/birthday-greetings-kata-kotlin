package it.eureka.katas.birthdaygreeting

import assertk.assertThat
import assertk.assertions.isEqualTo
import it.msec.kio.result.Failure
import it.msec.kio.result.Success
import it.msec.kio.runtime.Runtime.unsafeRunSync
import org.junit.Test

class ReadCsvTest {

    @Test
    fun `non existing file`() {
        assertThat(
            unsafeRunSync(readCsv(FileName("/fixtures/a_non_existing_file")))
        ).isEqualTo(
            Failure(ReadFileError("/fixtures/a_non_existing_file"))
        )
    }

    @Test
    fun `empty file so empty csv`() {
        assertThat(
            unsafeRunSync(readCsv(FileName("/fixtures/emptyFile.csv")))
        ).isEqualTo(
            Success(CsvFile(listOf()))
        )
    }

    @Test
    internal fun `filled csv`() {
        assertThat(
            unsafeRunSync(readCsv(FileName("/fixtures/goodFile.csv")))
        ).isEqualTo(
            Success(
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