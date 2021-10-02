package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.sequenceEither
import arrow.fx.coroutines.bracket
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@JvmInline value class FileName(val path: String)
data class CsvFile(val rows: List<CsvLine>)
@JvmInline value class CsvLine(val raw: String)

typealias ReadCsv = suspend (FileName) -> Either<ProgramError, CsvFile>
typealias ParseEmployee = suspend (CsvLine) -> Either<ProgramError, Employee>

inline fun createLoadEmployees(
    crossinline readCsv: ReadCsv,
    crossinline parseEmployee: ParseEmployee
): LoadEmployees = { sourceFile: FileName ->
    either {
        val file: CsvFile = readCsv(sourceFile).bind()
        file.rows
            .map { r -> parseEmployee(r) }
            .sequenceEither()
            .bind()
    }
}

suspend fun readCsv(file: FileName): Either<ProgramError, CsvFile> =
    Either.catch {
        bracket(
            acquire = { object {}.javaClass.getResource(file.path).openStream() },
            use = { inputStream ->
                inputStream.reader().readLines()
                    .drop(1)
                    .map(::CsvLine)
                    .let(::CsvFile)
            },
            release = { r: InputStream -> r.close() }
        )
    }.mapLeft { ReadFileError(file.path) }

fun parseEmployee(csvLine: CsvLine): Either<ProgramError, Employee> =
    csvLine.raw.split(",")
        .map { it.trim() }
        .let { csvLineCols ->
            Either.catch {
                Employee(
                    lastName = csvLineCols[0],
                    firstName = csvLineCols[1],
                    birthDate = LocalDate.parse(
                        csvLineCols[2],
                        DateTimeFormatter.ofPattern("yyyy/MM/dd")
                    ),
                    emailAddress = EmailAddress(csvLineCols[3])
                )
            }.mapLeft { ParseError(csvLine.raw) }
        }

