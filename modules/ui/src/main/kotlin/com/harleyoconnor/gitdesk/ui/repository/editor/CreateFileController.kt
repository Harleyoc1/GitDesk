package com.harleyoconnor.gitdesk.ui.repository.editor

import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.form.validation.FileNameAvailableValidator
import com.harleyoconnor.gitdesk.ui.node.TextField
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.Directory
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import java.io.File

/**
 * @author Harley O'Connor
 */
open class CreateFileController : ViewController<CreateFileController.Context> {

    object Loader : ResourceViewLoader<Context, CreateFileController, VBox>(
        UIResource("/ui/layouts/repository/editor/CreateFile.fxml")
    )

    class Context(
        val directory: Directory,
        val creationCallback: (File) -> Unit,
        val cancellationCallback: () -> Unit
    ) : ViewController.Context

    private lateinit var directory: Directory
    private lateinit var creationCallback: (File) -> Unit
    private lateinit var cancellationCallback: () -> Unit

    @FXML
    private lateinit var nameField: TextField

    @FXML
    private lateinit var createButton: Button

    override fun setup(context: Context) {
        directory = context.directory
        creationCallback = context.creationCallback
        cancellationCallback = context.cancellationCallback

        nameField.setOrAppendValidator(
            FileNameAvailableValidator(directory)
        )
    }

    @FXML
    private fun cancel(event: ActionEvent) {
        cancellationCallback()
    }

    @FXML
    private fun create(event: ActionEvent) {
        val name = nameField.getTextOrNull() ?: return
        File(directory.canonicalPath + File.separatorChar + name).also { file ->
            create(file)
            creationCallback(file)
        }
    }

    protected open fun create(file: File) {
        file.createNewFile()
    }

    @FXML
    private fun onKeyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            createButton.fire()
        }
    }

}