package com.harleyoconnor.gitdesk.util

import java.io.File

/**
 * An abstract representation of directory pathnames.
 *
 * @author Harley O'Connor
 */
class Directory : File {

    constructor(path: String) : super(path) {
        assertIsDirectory(this)
    }

    constructor(file: File) : super(file.path) {
        assertIsDirectory(this)
    }

    companion object {
        private fun assertIsDirectory(directory: File) {
            assert(directory.isDirectory) {
                "File is not a directory."
            }
        }
    }

    fun getChild(relativePath: String): Directory {
        return Directory(File(this.absolutePath + File.separatorChar + relativePath))
    }

}