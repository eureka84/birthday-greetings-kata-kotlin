package it.eureka.katas.birthdaygreeting

import arrow.core.extensions.list.traverse.sequence
import arrow.core.fix
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.functor.functor
import arrow.fx.extensions.io.monad.monad
import arrow.mtl.EitherT
import arrow.mtl.extensions.eithert.applicative.applicative
import arrow.mtl.fix

fun <E, A> List<EitherT<E, ForIO, A>>.sequence(): EitherT<E, ForIO, List<A>> {
    val applicative = EitherT.applicative<E, ForIO>(IO.monad())
    return this.sequence(applicative).fix().map(IO.functor()) { it.fix() }
}

fun <E, A, B> EitherT<E, ForIO, A>.map(f: (A) -> B): EitherT<E, ForIO, B> =
    this.map(IO.functor(), f)

fun <E, A, B> EitherT<E, ForIO, A>.flatMap(f: (A) -> EitherT<E, ForIO, B>): EitherT<E, ForIO, B> =
    this.flatMap(IO.monad(), f)