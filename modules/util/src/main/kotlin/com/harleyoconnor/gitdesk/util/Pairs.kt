package com.harleyoconnor.gitdesk.util

fun <A, B> Pair<A, B>.forFirst(action: (A) -> Unit): Pair<A, B> {
    action(this.first)
    return this
}

fun <A, B> Pair<A, B>.forSecond(action: (B) -> Unit): Pair<A, B> {
    action(this.second)
    return this
}