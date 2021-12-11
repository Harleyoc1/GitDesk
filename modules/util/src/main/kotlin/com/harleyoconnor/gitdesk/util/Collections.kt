package com.harleyoconnor.gitdesk.util

fun <E> MutableCollection<E>.addIfAbsent(element: E) {
    if (!this.contains(element)) {
        this.add(element)
    }
}