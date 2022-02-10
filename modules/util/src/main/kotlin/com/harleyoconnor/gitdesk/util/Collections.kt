package com.harleyoconnor.gitdesk.util

fun <E> MutableCollection<E>.addIfAbsent(element: E) {
    if (!this.contains(element)) {
        this.add(element)
    }
}

fun <E> MutableList<E>.addIfAbsent(index: Int, element: E) {
    if (!this.contains(element)) {
        this.add(index, element)
    }
}