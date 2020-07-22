package it.eureka.katas.birthdaygreeting

import arrow.core.Either
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.list.traverse.sequence
import arrow.core.fix

fun <E, A> List<Either<E, A>>.sequence(): Either<E, List<A>> {
    return this.sequence(Either.applicative()).fix().map { it.fix() }
}

infix fun <A, B, C> ((A) -> B).andThen(g: suspend (B) -> C ): suspend (A) -> C {
    val self= this
    return { a -> g(self(a)) }
}
