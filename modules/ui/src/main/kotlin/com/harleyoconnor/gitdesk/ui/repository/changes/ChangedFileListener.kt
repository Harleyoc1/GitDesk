package com.harleyoconnor.gitdesk.ui.repository.changes

import javafx.scene.Node

/**
 *
 * @author Harley O'Connor
 */
interface ChangedFileListener {

    fun onFileStaged()

    fun onFileUnStaged()

    fun onFileReset()

}