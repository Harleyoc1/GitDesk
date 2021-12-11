package com.harleyoconnor.gitdesk.data.syntax

import com.harleyoconnor.gitdesk.util.syntax.SyntaxHighlighter
import java.nio.file.Path

class SyntaxHighlighterHolder(
    syntaxHighlightingDirectory: Path
) : SyntaxHighlighterAccess {

    private val dataLoader: SyntaxHighlightingLoader = SyntaxHighlightingLoader(syntaxHighlightingDirectory)

    // TODO: Set that retains order based on Comparator/Comparable.
    private val highlighters: MutableSet<SyntaxHighlighter> = mutableSetOf()

    override fun load() {
        this.highlighters.clear()
        val data = this.dataLoader.load()
        this.highlighters.addAll(data.highlighters)
    }

    override fun save() {
        TODO("Not yet implemented")
    }

    override fun add(highlighter: SyntaxHighlighter) {
        this.highlighters.add(highlighter)
    }

    override fun get(fileName: String): SyntaxHighlighter? {
        return this.highlighters.sortedDescending().find {
            it.shouldHighlightFor(fileName)
        }
    }

}