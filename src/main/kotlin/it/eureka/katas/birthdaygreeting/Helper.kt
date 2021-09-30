package it.eureka.katas.birthdaygreeting

infix fun <A, B, C> ((A) -> B).andThen(g: suspend (B) -> C ): suspend (A) -> C {
    val self= this
    return { a -> g(self(a)) }
}
