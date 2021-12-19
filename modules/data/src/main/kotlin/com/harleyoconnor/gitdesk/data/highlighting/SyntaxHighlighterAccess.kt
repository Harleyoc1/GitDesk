package com.harleyoconnor.gitdesk.data.highlighting

import com.harleyoconnor.gitdesk.data.serialisation.DataAccess
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.stream
import com.harleyoconnor.gitdesk.util.syntax.SyntaxHighlighter

/**
 *
 * @author Harley O'Connor
 */
class SyntaxHighlighterAccess(
    directory: Directory
): DataAccess<String, SyntaxHighlighter> {

    private val serialiser = SyntaxHighlighterSerialiser(directory)
    private val deserialiser = SyntaxHighlighterDeserialiser(directory)

    private val syntaxHighlighters: MutableSet<SyntaxHighlighter> = mutableSetOf()

    init {
        directory.stream()
            .filter { it.extension == "json" }
            .forEach {
                syntaxHighlighters.add(get(it.nameWithoutExtension))
            }
    }

    override fun get(key: String): SyntaxHighlighter {
        return deserialiser.deserialise(key)
    }

    override fun save(key: String, data: SyntaxHighlighter) {
        serialiser.serialise(key, data)
    }

    fun getForFile(fileName: String): SyntaxHighlighter? {
        return this.syntaxHighlighters.sortedDescending().find {
            it.shouldHighlightFor(fileName)
        }
    }
}