package com.harleyoconnor.gitdesk.util

fun String.splitToPair(delimiter: Char): Pair<String, String>  {
    val splitString = this.split(delimiter)
    assert(splitString.size == 2)
    return splitString.first() to splitString.last()
}

fun String.splitToPair(delimiter: String): Pair<String, String>  {
    val splitString = this.split(delimiter)
    assert(splitString.size == 2)
    return splitString.first() to splitString.last()
}

fun <A, B> Pair<A, B>.forFirst(action: (A) -> Unit): Pair<A, B> {
    action(this.first)
    return this
}

fun <A, B> Pair<A, B>.forSecond(action: (B) -> Unit): Pair<A, B> {
    action(this.second)
    return this
}

fun <T, N> Pair<T, T>.mapBoth(mapper: (T) -> N): Pair<N, N> {
    return mapper(this.first) to mapper(this.second)
}