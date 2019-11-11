package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import arrow.core.extensions.fx
import arrow.core.flatMap
import arrow.fx.IO.Companion.effect
import java.time.LocalDate
import java.time.format.DateTimeFormatter

typealias ReadCsv = (FileName) -> Either<ProgramError, CsvFile>
typealias ParseEmployee = (CsvLine) -> Either<ProgramError, Employee>

inline fun loadEmployees(crossinline readCsv: ReadCsv, crossinline parseEmployee: ParseEmployee): LoadEmployees =
    { sourceFile: FileName ->
        readCsv(sourceFile)
            .flatMap { file ->
                file.rows
                    .map(parseEmployee)
                    .sequence()
            }
    }

fun readCsv(file: FileName): Either<ProgramError, CsvFile> =
    Either.fx {
        !effect {
            Either.catch {
                object {}.javaClass.getResource(file.path).readText()
            }.bimap(
                { ReadFileError(file.path) as ProgramError },
                { text ->
                    text.split("\n")
                        .drop(1)
                        .map(::CsvLine)
                        .let(::CsvFile)
                }
            )
        }.unsafeRunSync()
    }


fun parseEmployee(csvLine: CsvLine): Either<ProgramError, Employee> =
    csvLine.raw.split(",")
        .map { it.trim() }
        .let { csvLineCols ->
            Either.fx {
                !effect {
                    Either.catch {
                        Employee(
                            lastName = csvLineCols[0],
                            firstName = csvLineCols[1],
                            birthDate = LocalDate.parse(csvLineCols[2], DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                            emailAddress = EmailAddress(csvLineCols[3])
                        )
                    }.mapLeft { ParseError(csvLine.raw) }
                }.unsafeRunSync()
            }
        }

data class FileName(val path: String)
data class CsvFile(val rows: List<CsvLine>)
data class CsvLine(val raw: String)
