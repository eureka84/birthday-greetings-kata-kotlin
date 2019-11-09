package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.list.traverse.sequence
import arrow.core.fix

fun <E, A> List<Either<E, A>>.sequence(): Either<E, List<A>> =
    this.sequence(Either.applicative()).fix().map { it.fix() }