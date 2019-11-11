package it.eureka.katas.birthdaygreeting

import it.msec.kio.*
import it.msec.kio.common.list.sequence
import java.time.LocalDate
import java.time.format.DateTimeFormatter

typealias ReadCsv = (FileName) -> IO<ProgramError, CsvFile>
typealias ParseEmployee = (CsvLine) -> IO<ProgramError, Employee>

inline fun loadEmployees(crossinline readCsv: ReadCsv, crossinline parseEmployee: ParseEmployee): LoadEmployees =
    { sourceFile: FileName ->
        readCsv(sourceFile).flatMap { file: CsvFile ->
            file.rows
                .map(parseEmployee)
                .sequence()
        }
    }

fun readCsv(file: FileName): IO<ProgramError, CsvFile> =
    unsafe {
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

fun parseEmployee(csvLine: CsvLine): IO<ProgramError, Employee> =
    csvLine.raw.split(",")
        .map { it.trim() }
        .let { csvLineCols ->
            unsafe {
                Employee(
                    lastName = csvLineCols[0],
                    firstName = csvLineCols[1],
                    birthDate = LocalDate.parse(csvLineCols[2], DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                    emailAddress = EmailAddress(csvLineCols[3])
                )
            }.mapError { ParseError(csvLine.raw) }
        }

data class FileName(val path: String)
data class CsvFile(val rows: List<CsvLine>)
data class CsvLine(val raw: String)
