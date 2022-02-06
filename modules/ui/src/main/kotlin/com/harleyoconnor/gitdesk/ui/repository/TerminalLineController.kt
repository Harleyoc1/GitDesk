package com.harleyoconnor.gitdesk.ui.repository

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.scene.text.Font

/**
 * @author Harley O'Connor
 */
class TerminalLineController : ViewController<TerminalLineController.Context> {

    object Loader : ResourceViewLoader<Context, TerminalLineController, ScrollPane>(
        UIResource("/ui/layouts/repository/TerminalLine.fxml")
    )

    class Context(val repository: LocalRepository, val output: String, val enterPressedCallback: ((String) -> Unit)?) :
        ViewController.Context

    private lateinit var repository: LocalRepository
    private var enterPressedCallback: ((String) -> Unit)? = null

    @FXML
    private lateinit var root: HBox

    @FXML
    private lateinit var outputLabel: Label

    @FXML
    private lateinit var inputField: TextField

    override fun setup(context: Context) {
        repository = context.repository
        enterPressedCallback = context.enterPressedCallback

        outputLabel.text = context.output
        if (enterPressedCallback == null) {
            root.children.remove(inputField)
        }
        inputField.styleClass.removeAll("text-field", "text-input")
    }

    fun focus() {
        inputField.requestFocus()
    }

    @FXML
    private fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            enterPressedCallback?.invoke(inputField.text)
            outputLabel.text += inputField.text
            root.children.remove(inputField)
        }
    }

}