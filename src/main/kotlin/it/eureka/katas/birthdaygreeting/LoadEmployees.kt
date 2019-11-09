package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.list.traverse.sequence
import arrow.core.fix
import arrow.core.flatMap
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

typealias LoadEmployees = suspend (FileName) -> Either<ProgramError, List<Employee>>
typealias ReadCsv = suspend (FileName) -> Either<ProgramError, CsvFile>
typealias ParseEmployee = suspend (CsvLine) -> Either<ProgramError, Employee>

inline fun loadEmployees(crossinline readCsv: ReadCsv, crossinline parseEmployee: ParseEmployee): LoadEmployees =
    { sourceFile: FileName ->
        readCsv(sourceFile)
            .flatMap { file ->
                file.rows.map { row ->
                    runBlocking { parseEmployee(row) }
                }.sequence()
            }
    }

fun <E, A> List<Either<E, A>>.sequence(): Either<E, List<A>> =
    this.sequence(Either.applicative()).fix().map { it.fix() }

suspend fun readCsv(file: FileName): Either<ProgramError, CsvFile> =
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
            }.mapLeft { ParseError(csvLine.raw) as ProgramError }
        }

data class FileName(val path: String)
data class CsvFile(val rows: List<CsvLine>)
data class CsvLine(val raw: String)
