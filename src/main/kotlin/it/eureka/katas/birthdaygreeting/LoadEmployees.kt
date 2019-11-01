package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

inline fun loadEmployees(
    crossinline readCsv: (FileName) -> IOEither<ProgramError, CsvFile>,
    crossinline parseEmployee: (CsvLine) -> IOEither<ProgramError, Employee>
): (FileName) -> IOEither<ProgramError, List<Employee>> = { sourceFile: FileName ->
    readCsv(sourceFile)
        .flatMap { file ->
            file.rows.map(parseEmployee).sequence()
        }
}

fun readCsv(file: FileName): IOEither<ProgramError, CsvFile> =
    runBlocking {
        IOEitherFrom(
            Either.catch {
                object {}.javaClass.getResource(file.path).readText()
            }.bimap(
                { ReadFileError(file.path) as ProgramError },
                { text ->
                    text.split("\n")
                        .drop(1)
                        .map(::CsvLine)
                        .let(::CsvFile)
                })
        )
    }

fun parseEmployee(csvLine: CsvLine): IOEither<ProgramError, Employee> =
    runBlocking {
        IOEitherFrom(
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
                    }.mapLeft { ParseError(csvLine.raw) as ProgramError }
                }
        )
    }

data class FileName(val path: String)
data class CsvFile(val rows: List<CsvLine>)
data class CsvLine(val raw: String)