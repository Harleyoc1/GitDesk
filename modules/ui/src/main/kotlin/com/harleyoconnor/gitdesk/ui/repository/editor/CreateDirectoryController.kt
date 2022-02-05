package com.harleyoconnor.gitdesk.ui.repository.editor

import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import javafx.scene.layout.VBox
import java.io.File

/**
 * @author Harley O'Connor
 */
open class CreateDirectoryController : CreateFileController() {

    object Loader : ResourceViewLoader<Context, CreateDirectoryController, VBox>(
        UIResource("/ui/layouts/repository/editor/CreateDirectory.fxml")
    )

    override fun create(file: File) {
        file.mkdirs()
    }

}