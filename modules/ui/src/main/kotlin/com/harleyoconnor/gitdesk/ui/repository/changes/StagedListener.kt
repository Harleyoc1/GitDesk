package com.harleyoconnor.gitdesk.ui.repository.changes

/**
 *
 * @author Harley O'Connor
 */
interface StagedListener {

    fun onFileStaged()

    fun onFileUnStaged()

}