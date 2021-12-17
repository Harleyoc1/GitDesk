package com.harleyoconnor.gitdesk.util

fun <I : Any?, O> I.map(mapper: (I) -> O): O {
    return mapper(this)
}