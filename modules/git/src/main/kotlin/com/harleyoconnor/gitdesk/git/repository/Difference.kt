package com.harleyoconnor.gitdesk.git.repository

import com.harleyoconnor.gitdesk.util.indexOf
import com.harleyoconnor.gitdesk.util.mapBoth
import com.harleyoconnor.gitdesk.util.split
import com.harleyoconnor.gitdesk.util.splitToPair
import com.harleyoconnor.gitdesk.util.toTypedArray

/**
 * @author Harley O'Connor
 */
class Difference(
    val hunks: Array<Hunk>
) {

    companion object {
        fun parse(difference: String): Difference {
            val hunks = difference.split("\n").split { it.startsWith('@') }.toMutableList()
            hunks.removeFirst() // First section is not a hunk but the header.
            return Difference(
                hunks.stream().map { Hunk.parse(it.toMutableList()) }.toTypedArray()
            )
        }
    }

    override fun toString(): String {
        return "Difference(hunks=${hunks.contentToString()})"
    }

    class Hunk(
        val header: Header,
        val lines: Array<Line>
    ) {
        companion object {
            fun parse(hunk: MutableList<String>): Hunk {
                val firstLine = hunk.first()
                val endOfHeaderIndex = firstLine.indexOf(4, '@')  + 1
                val header = Header.parse(firstLine.substring(0, endOfHeaderIndex))
                if (firstLine.length > endOfHeaderIndex) {
                    hunk[0] = firstLine.substring(endOfHeaderIndex)
                } else {
                    hunk.removeAt(0)
                }
                return Hunk(header, readLines(hunk))
            }

            private fun readLines(lines: List<String>) = lines.stream()
                .map { parseLine(it) }
                .toTypedArray()

            private fun parseLine(line: String): Line {
                return when {
                    (line.startsWith('-')) -> SubtractedLine(line.substring(1))
                    (line.startsWith('+')) -> AddedLine(line.substring(1))
                    else -> UnchangedLine(line.substring(1))
                }
            }

        }

        class Header(
            val fromFileStartLine: Int,
            val fromFileLines: Int,
            val toFileStartLine: Int,
            val toFileLines: Int
        ) {
            companion object {
                /**
                 * Parses the specified [hunk header][header].
                 *
                 * @param header the hunk header, in the form `@@ -<from file data> +<to file data> @@`
                 *               (see [parseFileData] for specification of file data format)
                 */
                fun parse(header: String): Header {
                    val headerContent = header
                        .removePrefix("@@ ")
                        .removeSuffix(" @@")
                        .splitToPair(' ')
                    val fromFile = parseFileData(headerContent.first.removePrefix("-"))
                    val toFile = parseFileData(headerContent.second.removePrefix("+"))
                    return Header(fromFile.first, fromFile.second, toFile.first, toFile.second)
                }

                /**
                 * Parses the specified [data] about a particular file from the hunk header.
                 *
                 * @param data the file data, in the form `<start line>,<number of lines>`, or `<start line>` where the
                 *             number of lines defaults to `1`
                 * @return the parsed data, in a pair with the first integer being the start line and the second the
                 *         number of lines
                 */
                private fun parseFileData(data: String): Pair<Int, Int> {
                    return if (!data.contains(",")) {
                        data.toInt() to 1
                    } else data.splitToPair(',').mapBoth { it.toInt() }
                }

            }

            override fun toString(): String {
                return "Header(fromFileStartLine=$fromFileStartLine, fromFileLines=$fromFileLines, toFileStartLine=$toFileStartLine, toFileLines=$toFileLines)"
            }

        }

        override fun toString(): String {
            return "Hunk(header=$header, lines=${lines.contentToString()})"
        }
    }

    interface Line {
        val text: String
    }

    data class UnchangedLine(override val text: String) : Line

    data class SubtractedLine(override val text: String) : Line

    data class AddedLine(override val text: String) : Line

}