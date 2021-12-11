package com.harleyoconnor.gitdesk.data.syntax

import com.harleyoconnor.gitdesk.data.DataManager
import com.harleyoconnor.gitdesk.util.syntax.SyntaxHighlighter

/**
 * @author Harley O'Connor
 */
interface SyntaxHighlighterAccess : DataManager {

    fun add(highlighter: SyntaxHighlighter)

    fun get(fileName: String): SyntaxHighlighter?

}