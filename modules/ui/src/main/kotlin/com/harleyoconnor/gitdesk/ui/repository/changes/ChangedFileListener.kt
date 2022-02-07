package com.harleyoconnor.gitdesk.ui.repository.changes

/**
 *
 * @author Harley O'Connor
 */
interface ChangedFileListener {

    fun onFileStaged()

    fun onFileUnStaged()

    fun onFileReset()

}