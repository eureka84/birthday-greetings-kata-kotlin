package it.eureka.katas.birthdaygreeting

import arrow.core.left
import arrow.core.right
import arrow.fx.coroutines.Environment
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test

class ReadCsvTest {

    @Test
    fun `non existing file`() {
        Environment().unsafeRunSync {
            assertThat(readCsv(FileName("/fixtures/a_non_existing_file")))
                .isEqualTo(ReadFileError("/fixtures/a_non_existing_file").left())
        }
    }

    @Test
    fun `empty file so empty csv`() {
        Environment().unsafeRunSync {
            assertThat(readCsv(FileName("/fixtures/emptyFile.csv")))
                .isEqualTo(CsvFile(listOf()).right())
        }
    }

    @Test
    fun `filled csv`() {
        Environment().unsafeRunSync {
            assertThat(readCsv(FileName("/fixtures/goodFile.csv")))
                .isEqualTo(
                    CsvFile(
                        listOf(
                            CsvLine("Doe, John, 1982/10/08, john.doe@foobar.com"),
                            CsvLine("Ann, Mary, 1975/09/11, mary.ann@foobar.com")
                        )
                    ).right()
                )
        }
    }
}