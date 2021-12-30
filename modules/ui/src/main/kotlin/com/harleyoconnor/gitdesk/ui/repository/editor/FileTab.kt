package com.harleyoconnor.gitdesk.ui.repository.editor

import javafx.scene.control.Tab
import java.io.File

/**
 * @author Harley O'Connor
 */
class FileTab: Tab() {

    private lateinit var file: File
    private lateinit var saveCallback: () -> Unit

    init {
        this.isClosable = false // We use a custom close button.
    }

    fun setFile(file: File) {
        this.file = file
    }

    fun getFile(): File = file

    fun setSaveCallback(saveCallback: () -> Unit) {
        this.saveCallback = saveCallback
    }

    fun getSaveCallback(): () -> Unit = saveCallback

}