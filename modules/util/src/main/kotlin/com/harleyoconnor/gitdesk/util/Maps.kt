package com.harleyoconnor.gitdesk.util

infix fun <K, V> Map<K, V>.with(other: Map<K, V>): Map<K, V> {
    return this.toMutableMap().also {
        it.putAll(other)
    }
}