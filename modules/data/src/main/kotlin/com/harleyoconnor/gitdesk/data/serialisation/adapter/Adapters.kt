package com.harleyoconnor.gitdesk.data.serialisation.adapter

import com.harleyoconnor.gitdesk.data.serialisation.qualifier.DirectoryTree
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.tree.MutableTree
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.text.SimpleDateFormat

/** [DateFormat][java.text.DateFormat] for the [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601) standard. */
val ISO8601_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

fun Moshi.Builder.addExtraAdapters(): Moshi.Builder {
    this.add(DateAdapter(ISO8601_FORMAT))
    this.add(FileAdapter)
    this.add(DirectoryAdapter)
    this.add(PatternAdapter)
    this.add(URLAdapter)
    this.add(MutableTreeAdapter.FACTORY)
    this.add(TreeAdapter.FACTORY)
    this.add(RepositoryNameAdapter)
    this.add(
        Types.newParameterizedType(MutableTree::class.java, Directory::class.java),
        DirectoryTree::class.java,
        DirectoryTreeAdapter
    )
    return this
}