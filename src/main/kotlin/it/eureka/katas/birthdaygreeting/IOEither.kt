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
import arrow.mtl.extensions.EitherTApplicative
import arrow.mtl.extensions.eithert.applicative.applicative
import arrow.mtl.fix

typealias IOEither<E, A> = EitherT<ForIO, E, A>

fun <E, A> IOEitherFrom(e: Either<E, A>): IOEither<E, A> =
    EitherT.fromEither(IO.applicative(), e)

fun <E, A, B> IOEither<E, A>.map(f: (A) -> B): IOEither<E, B> = this.map(IO.functor(), f)
fun <E, A, B> IOEither<E, A>.flatMap(f: (A) -> IOEither<E, B>): IOEither<E, B> = this.flatMap(IO.monad(), f)
fun <E, A> IOEither<E, A>.run(): Either<E, A> = this.value().fix().unsafeRunSync()

fun <E, A> List<IOEither<E, A>>.sequence(): IOEither<E, List<A>> {
    val applicative: EitherTApplicative<ForIO, E> = EitherT.applicative(IO.applicative())
    return this.sequence(applicative).fix().map {it.fix()}
}