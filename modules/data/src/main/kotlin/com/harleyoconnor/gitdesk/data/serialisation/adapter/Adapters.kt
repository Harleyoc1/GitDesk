package com.harleyoconnor.gitdesk.data.serialisation.adapter

import com.harleyoconnor.gitdesk.data.serialisation.qualifier.DirectoryTree
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.syntax.SyntaxHighlighter
import com.harleyoconnor.gitdesk.util.tree.MutableTree
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

fun Moshi.Builder.addExtraAdapters(): Moshi.Builder {
    this.add(ISO8601DateAdapter)
    this.add(FileAdapter)
    this.add(DirectoryAdapter)
    this.add(PatternAdapter)
    this.add(SyntaxHighlighter::class.java, SyntaxHighlighterAdapter)
    this.add(MutableTreeAdapter.FACTORY)
    this.add(TreeAdapter.FACTORY)
    this.add(
        Types.newParameterizedType(MutableTree::class.java, Directory::class.java),
        DirectoryTree::class.java,
        DirectoryTreeAdapter
    )
    return this
}