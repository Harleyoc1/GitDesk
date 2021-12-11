package com.harleyoconnor.gitdesk.util.syntax

/**
 *
 * @author Harley O'Connor
 */
interface SyntaxHighlighter : Comparable<SyntaxHighlighter> {

    val priority: Int

    fun shouldHighlightFor(fileName: String): Boolean

    fun highlight(text: String): Any

}