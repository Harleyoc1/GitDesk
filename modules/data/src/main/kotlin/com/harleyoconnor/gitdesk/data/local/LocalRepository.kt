package com.harleyoconnor.gitdesk.data.local

import com.harleyoconnor.gitdesk.data.WindowCache
import com.harleyoconnor.gitdesk.data.serialisation.qualifier.DirectoryTree
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.tree.MutableArrayTree
import com.harleyoconnor.gitdesk.util.tree.MutableTree
import com.squareup.moshi.Json
import java.util.Date

/**
 * @author Harley O'Connor
 */
class LocalRepository(
    val id: String,
    val directory: Directory,
    @Json(name = "last_open") private var lastOpen: Date? = null,
    @DirectoryTree @Json(name = "open_directories") val openDirectories: MutableTree<Directory> =
        MutableArrayTree(directory),
    @Json(name = "window_cache") val windowCache: WindowCache = WindowCache() // TODO: Cache all windows separately.
) {

    @Transient
    var open: Boolean = false

    fun getLastOpen(): Date? {
        return this.lastOpen
    }

    fun close() {
        this.lastOpen = Date()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocalRepository

        if (directory != other.directory) return false

        return true
    }

    override fun hashCode(): Int {
        return directory.hashCode()
    }

}