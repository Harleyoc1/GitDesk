package com.harleyoconnor.gitdesk.util

import java.net.URL

/**
 * Finds the index of the `n`th [occurrence] of the specified [char] in this sequence.
 *
 * @param occurrence the occurrence of the specified [char] to find
 * @param char the character to find in this sequence
 * @return the index of the `n`th [occurrence] the specified [char]; otherwise `-1` if there was not an `n`th
 * [occurrence]
 */
fun CharSequence.indexOf(occurrence: Int, char: Char): Int {
    var current = 1

    for (i in this.indices) {
        if (this[i] == char) {
            if (current == occurrence) {
                return i
            }
            current++
        }
    }

    return -1
}

fun URL.toGitDisplayUrl(): String =
    this.toExternalForm().substringAfter('@').removePrefix("https://").removeSuffix(".git")

fun String.substringUntil(startIndex: Int, endChar: Char): String {
    return substring(startIndex).substringBefore(endChar)
}

fun Int.toHexColourString(): String {
    return String.format("#%06X", (0xFFFFFF and this))
}