package com.harleyoconnor.gitdesk.ui.repository.editor

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.CodeEditor
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.fxml.FXML
import javafx.scene.Node
import java.io.File

/**
 * @author Harley O'Connor
 */
class FileEditorController : ViewController<FileEditorController.Context> {

    object Loader: ResourceViewLoader<Context, FileEditorController, Node>(
        UIResource("/ui/layouts/repository/editor/FileEditor.fxml")
    )

    class Context(val file: File): ViewController.Context

    private lateinit var file: File

    @FXML
    private lateinit var editor: CodeEditor

    override fun setup(context: Context) {
        this.file = context.file
        loadFromFile(file)
    }

    private fun loadFromFile(file: File) {
        Data.syntaxHighlighterAccess.getForFile(file.name)?.let {
            this.editor.setupSyntaxHighlighting(it)
        }
        this.editor.replaceText(file.readLines().joinToString(separator = "\n"))
    }

    fun saveToFile() {
        this.file.writeText(this.editor.text)
    }

}