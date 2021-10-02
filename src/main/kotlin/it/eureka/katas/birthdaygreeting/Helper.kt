package it.eureka.katas.birthdaygreeting

infix fun <A, B, C> ((A) -> B).andThen(g: suspend (B) -> C ): suspend (A) -> C = { a -> g(this(a)) }
