package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import arrow.core.extensions.list.traverse.sequence
import arrow.core.fix
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.functor.functor
import arrow.fx.extensions.io.monad.monad
import arrow.fx.fix
import arrow.mtl.EitherT
import arrow.mtl.extensions.eithert.applicative.applicative
import arrow.mtl.fix
import java.time.LocalDate
import java.time.format.DateTimeFormatter

typealias ReadCsv = (FileName) -> IO<Either<ProgramError, CsvFile>>
typealias ParseEmployee = (CsvLine) -> IO<Either<ProgramError, Employee>>

inline fun loadEmployees(crossinline readCsv: ReadCsv, crossinline parseEmployee: ParseEmployee): LoadEmployees =
    { sourceFile: FileName ->
        EitherT(readCsv(sourceFile)).flatMap(IO.monad()) { file: CsvFile ->
            file.rows
                .map { r -> EitherT(parseEmployee(r)) }
                .sequence()
        }.value().fix()
    }

fun <A> List<EitherT<ForIO, ProgramError, A>>.sequence(): EitherT<ForIO, ProgramError, List<A>> {
    val applicative = EitherT.applicative<ForIO, ProgramError>(IO.applicative())
    return this.sequence(applicative).fix().map(IO.functor()) { it.fix() }
}

fun readCsv(file: FileName): IO<Either<ProgramError, CsvFile>> =
    IO {
        object {}.javaClass.getResource(file.path).readText()
    }.attempt().map {
        it.bimap(
            { ReadFileError(file.path) as ProgramError },
            { text ->
                text.split("\n")
                    .drop(1)
                    .map(::CsvLine)
                    .let(::CsvFile)
            }
        )
    }


fun parseEmployee(csvLine: CsvLine): IO<Either<ProgramError, Employee>> =
    csvLine.raw.split(",")
        .map { it.trim() }
        .let { csvLineCols ->
            IO {
                Employee(
                    lastName = csvLineCols[0],
                    firstName = csvLineCols[1],
                    birthDate = LocalDate.parse(csvLineCols[2], DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                    emailAddress = EmailAddress(csvLineCols[3])
                )
            }.attempt().map { it.mapLeft { ParseError(csvLine.raw) } }
        }

data class FileName(val path: String)
data class CsvFile(val rows: List<CsvLine>)
data class CsvLine(val raw: String)
