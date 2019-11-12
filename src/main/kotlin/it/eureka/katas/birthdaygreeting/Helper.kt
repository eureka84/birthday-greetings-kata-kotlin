package it.eureka.katas.birthdaygreeting

import arrow.core.extensions.list.traverse.sequence
import arrow.core.fix
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.functor.functor
import arrow.fx.extensions.io.monad.monad
import arrow.mtl.EitherT
import arrow.mtl.extensions.eithert.applicative.applicative
import arrow.mtl.fix

fun <A> List<EitherT<ForIO, ProgramError, A>>.sequence(): EitherT<ForIO, ProgramError, List<A>> {
    val applicative = EitherT.applicative<ForIO, ProgramError>(IO.applicative())
    return this.sequence(applicative).fix().map(IO.functor()) { it.fix() }
}

fun <E, A, B> EitherT<ForIO, E, A>.map(f: (A) -> B): EitherT<ForIO, E, B> =
    this.map(IO.functor(), f)

fun <E, A, B> EitherT<ForIO, E, A>.flatMap(f: (A) -> EitherT<ForIO, E, B>): EitherT<ForIO, E, B> =
    this.flatMap(IO.monad(), f)