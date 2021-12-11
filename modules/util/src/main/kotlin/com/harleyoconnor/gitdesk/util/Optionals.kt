package com.harleyoconnor.gitdesk.util

import java.util.Optional
import java.util.OptionalInt

fun <T> OptionalInt.map(mapper: (Int) -> T?): Optional<T> {
    return if (this.isPresent) Optional.ofNullable(mapper(this.asInt)) else Optional.empty()
}
