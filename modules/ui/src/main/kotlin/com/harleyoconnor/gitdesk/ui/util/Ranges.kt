package com.harleyoconnor.gitdesk.ui.util

import java.lang.RuntimeException
import java.util.regex.Pattern

private val validIntRangePattern: Pattern by lazy { Pattern.compile("\\[[0-9]+,[0-9]+]") }

class IntRange(
    val min: Int,
    val max: Int
)

fun parseIntRange(string: String): IntRange {
    assert(validIntRangePattern.matcher(string).matches()) {
        RuntimeException("$string not a valid int range.")
    }
    val separatorIndex = string.indexOf(',')
    return IntRange(
        string.substring(1, separatorIndex).ifBlank { "0" }.toInt(),
        string.substring(separatorIndex + 1, string.length - 1).ifBlank { Int.MAX_VALUE.toString() }.toInt()
    )
}

