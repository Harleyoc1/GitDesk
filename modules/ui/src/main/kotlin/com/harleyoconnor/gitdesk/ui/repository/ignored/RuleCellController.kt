package com.harleyoconnor.gitdesk.ui.repository.ignored

import com.harleyoconnor.gitdesk.git.repository.IgnoreFile
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.ui.view.ViewLoader
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.input.ContextMenuEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox

/**
 * @author Harley O'Connor
 */
class RuleCellController : ViewController<RuleCellController.Context> {

    companion object {
        fun load(
            rule: IgnoreFile.Rule,
            editCallback: (IgnoreFile.Rule) -> Unit,
            deleteCallback: (IgnoreFile.Rule) -> Unit
        ): ViewLoader.View<RuleCellController, HBox> {
            val context = Context(rule, editCallback, deleteCallback)
            return when (rule) {
                is IgnoreFile.DirectoryRule -> {
                    DirectoryLoader.load(context)
                }
                is IgnoreFile.FileExtensionRule -> {
                    FileExtensionLoader.load(context)
                }
                else -> Loader.load(context)
            }
        }
    }

    object Loader : ResourceViewLoader<Context, RuleCellController, HBox>(
        UIResource("/ui/layouts/repository/ignored/RuleCell.fxml")
    )

    object DirectoryLoader : ResourceViewLoader<Context, RuleCellController, HBox>(
        UIResource("/ui/layouts/repository/ignored/DirectoryRuleCell.fxml")
    )

    object FileExtensionLoader : ResourceViewLoader<Context, RuleCellController, HBox>(
        UIResource("/ui/layouts/repository/ignored/FileExtensionRuleCell.fxml")
    )

    class Context(
        val rule: IgnoreFile.Rule,
        val editCallback: (IgnoreFile.Rule) -> Unit,
        val deleteCallback: (IgnoreFile.Rule) -> Unit
    ) : ViewController.Context

    private lateinit var rule: IgnoreFile.Rule
    private lateinit var editCallback: (IgnoreFile.Rule) -> Unit
    private lateinit var deleteCallback: (IgnoreFile.Rule) -> Unit

    @FXML
    private lateinit var cell: HBox

    @FXML
    private lateinit var contextMenu: ContextMenu

    @FXML
    private lateinit var label: Label

    override fun setup(context: Context) {
        rule = context.rule
        editCallback = context.editCallback
        deleteCallback = context.deleteCallback

        label.text = rule.body
    }

    @FXML
    private fun onClicked(event: MouseEvent) {
        if (event.button == MouseButton.PRIMARY) {
            edit()
        }
    }

    @FXML
    private fun openContextMenu(event: ContextMenuEvent) {
        contextMenu.show(cell, event.screenX, event.screenY)
    }

    @FXML
    private fun edit() {
        editCallback(rule)
    }

    @FXML
    private fun delete(event: ActionEvent) {
        deleteCallback(rule)
    }


}