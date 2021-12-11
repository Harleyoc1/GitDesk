package com.harleyoconnor.gitdesk.data.syntax

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.util.syntax.SyntaxHighlighter
import com.squareup.moshi.JsonAdapter

/**
 * @author Harley O'Connor
 */
class SyntaxHighlighterType<T : SyntaxHighlighter>(
    val type: Class<T>
) {

    val adapter: JsonAdapter<T> by lazy { Data.moshi.adapter(type) }

}