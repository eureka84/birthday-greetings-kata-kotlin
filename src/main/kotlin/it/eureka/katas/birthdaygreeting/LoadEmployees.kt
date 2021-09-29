package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import arrow.core.computations.either
import arrow.fx.coroutines.Resource
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FileName(val path: String)
data class CsvFile(val rows: List<CsvLine>)
data class CsvLine(val raw: String)

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
            .sequence()
            .bind()
    }
}

suspend fun readCsv(file: FileName): Either<ProgramError, CsvFile> =
    Either.catch {
        Resource(
            acquire = { object {}.javaClass.getResource(file.path).openStream() },
            release = { r: InputStream -> r.close() }
        ).use { inputStream ->
            inputStream.reader().readLines()
                .drop(1)
                .map(::CsvLine)
                .let(::CsvFile)
        }
    }.mapLeft { ReadFileError(file.path) }

suspend fun parseEmployee(csvLine: CsvLine): Either<ProgramError, Employee> =
    csvLine.raw.split(",")
        .map { it.trim() }
        .let { csvLineCols ->
            Either.catch {
                Employee(
                    lastName = csvLineCols[0],
                    firstName = csvLineCols[1],
                    birthDate = LocalDate.parse(csvLineCols[2], DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                    emailAddress = EmailAddress(csvLineCols[3])
                )
            }.mapLeft { ParseError(csvLine.raw) }
        }

