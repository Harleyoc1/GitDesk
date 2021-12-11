package com.harleyoconnor.gitdesk.util

import java.util.Spliterator
import java.util.Spliterators
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.random.Random

fun <E> Array<E>.spliterator(): Spliterator<E> =
    Spliterators.spliterator(this, Spliterator.ORDERED)

fun <E> Array<E>.stream(): Stream<E> =
    StreamSupport.stream(this.spliterator(), false)

fun <E> Array<E>.shuffled(random: Random = Random): Array<E> {
    val clone = this.copyOf()
    clone.shuffle(random)
    return clone
}

fun <E> Array<E>.splitInHalf(): Pair<Array<E>, Array<E>> {
    return this.split(this.size / 2)
}

fun <E> Array<E>.split(index: Int): Pair<Array<E>, Array<E>> {
    return this.copyOfRange(0, index) to this.copyOfRange(index, this.size)
}

inline fun <reified E> Array<E>.with(vararg element: E): Array<E> {
    return arrayOf(*this, *element)
}

fun <E> Array<E>.forEachAfter(index: Int, action: (E) -> Unit) {
    this.copyOfRange(index + 1, this.size).forEach(action)
}