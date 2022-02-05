package com.harleyoconnor.gitdesk.ui.repository.editor

import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.util.Directory
import java.io.File

/**
 * @author Harley O'Connor
 */
class CreateDirectoryWindow(
    directory: Directory,
    creationCallback: (File) -> Unit
) : CreateFileWindow(directory, creationCallback) {

    override val id: String get() = "CreateDirectory"

    override var title: String = TRANSLATIONS_BUNDLE.getString("window.create_directory.title")

    override fun openView() {
        root = CreateDirectoryController.Loader.load(
            CreateFileController.Context(
                directory,
                { close(); creationCallback(it) },
                { close() }
            )
        ).root
    }

}