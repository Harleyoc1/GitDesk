package com.harleyoconnor.gitdesk.data.highlighting

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.util.syntax.SyntaxHighlighter
import com.squareup.moshi.JsonAdapter

/**
 *
 * @author Harley O'Connor
 */
object SyntaxHighlighterTypes {

    private val types: MutableMap<String, Type<*>> = mutableMapOf()

    fun register(key: String, type: Class<out SyntaxHighlighter>) {
        types[key] = Type(type)
    }

    @Suppress("UNCHECKED_CAST")
    fun get(key: String): Type<SyntaxHighlighter>? {
        return types[key] as? Type<SyntaxHighlighter>
    }

    @Suppress("UNCHECKED_CAST")
    fun getFor(highlighter: SyntaxHighlighter): Type<SyntaxHighlighter>? {
        return types.values.stream()
            .filter { it.type.isInstance(highlighter) }
            .findFirst()
            .map { it as? Type<SyntaxHighlighter> }
            .orElse(null)
    }

    open class Type<H>(val type: Class<H>) {
        val adapter: JsonAdapter<H> by lazy { MOSHI.adapter(type) }
    }

}