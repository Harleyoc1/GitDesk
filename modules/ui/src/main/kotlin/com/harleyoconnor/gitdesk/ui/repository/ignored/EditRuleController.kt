package com.harleyoconnor.gitdesk.ui.repository.ignored

import com.harleyoconnor.gitdesk.git.repository.IgnoreFile
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.TextField
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class EditRuleController : ViewController<EditRuleController.Context> {

    object Loader : ResourceViewLoader<Context, EditRuleController, VBox>(
        UIResource("/ui/layouts/repository/ignored/EditRule.fxml")
    )

    class Context(val rule: IgnoreFile.Rule, val saveCallback: (String) -> Unit, val cancelCallback: () -> Unit) :
        ViewController.Context

    private lateinit var saveCallback: (String) -> Unit
    private lateinit var cancelCallback: () -> Unit

    @FXML
    private lateinit var ruleField: TextField

    @FXML
    private lateinit var saveButton: Button

    override fun setup(context: Context) {
        saveCallback = context.saveCallback
        cancelCallback = context.cancelCallback

        ruleField.setText(context.rule.body)
    }

    @FXML
    private fun cancel(event: ActionEvent) {
        cancelCallback()
    }

    @FXML
    private fun save(event: ActionEvent) {
        val newBody = ruleField.getTextOrNull() ?: return
        saveCallback(newBody)
    }

    @FXML
    private fun onKeyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            saveButton.fire()
        }
    }
}