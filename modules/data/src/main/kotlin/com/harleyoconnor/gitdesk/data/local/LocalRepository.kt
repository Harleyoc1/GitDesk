package com.harleyoconnor.gitdesk.data.local

import com.harleyoconnor.gitdesk.data.WindowCache
import com.harleyoconnor.gitdesk.data.serialisation.qualifier.DirectoryTree
import com.harleyoconnor.gitdesk.git.repository.Repository
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
    @Json(name = "show_hidden_files") var showHiddenFiles: Boolean = false,
    @DirectoryTree @Json(name = "open_directories") val openDirectories: MutableTree<Directory> =
        MutableArrayTree(directory),
    @Json(name = "window_cache") var windowCache: WindowCache = WindowCache()
) {

    val gitRepository: Repository by lazy { Repository(directory) }

    @Transient
    var open: Boolean = false

    fun getLastOpen(): Date? {
        return this.lastOpen
    }

    fun close() {
        this.lastOpen = Date()
        this.open = false
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