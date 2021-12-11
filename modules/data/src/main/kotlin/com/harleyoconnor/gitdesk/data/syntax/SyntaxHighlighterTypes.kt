package com.harleyoconnor.gitdesk.data.syntax

import com.harleyoconnor.gitdesk.util.syntax.SyntaxHighlighter

/**
 * @author Harley O'Connor
 */
object SyntaxHighlighterTypes {

    private val types: MutableMap<String, SyntaxHighlighterType<*>> = mutableMapOf()

    fun register(id: String, type: SyntaxHighlighterType<*>) {
        types[id] = type
    }

    fun get(id: String): SyntaxHighlighterType<*>? {
        return types[id]
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : SyntaxHighlighter> typeFor(type: Class<T>): SyntaxHighlighterType<T>? {
        return types.entries.find { it.value.type == type }?.value as? SyntaxHighlighterType<T>
    }

    fun idFor(type: Class<SyntaxHighlighter>): String? {
        return types.entries.find { it.value.type == type }?.key
    }

}