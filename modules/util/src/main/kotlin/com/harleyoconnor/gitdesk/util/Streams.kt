package com.harleyoconnor.gitdesk.util

import java.util.LinkedList
import java.util.stream.Collectors
import java.util.stream.Stream

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Stream<T>.toTypedArray(): Array<T> {
    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    val thisCollection = this.collect(Collectors.toList()) as java.util.List<T>
    return thisCollection.toArray(arrayOfNulls<T>(0)) as Array<T>
}

/**
 * Splits this stream into a pair of streams by the specified [predicate].
 *
 * This is implemented by collecting the elements into two mutable lists, so if this stream is sequential the order is
 * retained.
 *
 * @param predicate the predicate to determine which stream a given element should be split into. This should return
 * `true` if the element should go in the first stream or `false` if it should go in the second.
 * @return the split pair of streams
 */
fun <T> Stream<T>.splitToPair(predicate: (T) -> Boolean): Pair<Stream<T>, Stream<T>> {
    val first = mutableListOf<T>()
    val second = mutableListOf<T>()

    this.forEach {
        if (predicate.invoke(it)) {
            first.add(it)
        } else {
            second.add(it)
        }
    }

    return first.stream() to second.stream()
}

fun <E> List<E>.split(delimiterPredicate: (E) -> Boolean): List<List<E>> {
    val new = LinkedList<MutableList<E>>()
    new.add(mutableListOf())
    for (element in this) {
        if (delimiterPredicate(element)) {
            new.add(mutableListOf())
        }
        new.peekLast().add(element)
    }
    return new
}

fun <T> Stream<T>.toSet(): Set<T> {
    return this.collect(Collectors.toSet())
}

fun <T> Stream<T>.toUnmodifiableSet(): Set<T> {
    return this.collect(Collectors.toUnmodifiableSet())
}

fun <T : Pair<K, V>, K, V> Stream<T>.pairsToMap(): Map<K, V> {
    return this.collect(Collectors.toMap({ it.first }, { it.second }))
}

fun <T : Map.Entry<K, V>, K, V> Stream<T>.entriesToMap(): Map<K, V> {
    return this.collect(Collectors.toMap({ it.key }, { it.value }))
}
