package com.harleyoconnor.gitdesk.ui.repository.branch

import com.harleyoconnor.gitdesk.git.repository.Branch
import com.harleyoconnor.gitdesk.git.repository.Repository
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.form.validation.BranchNameAvailableValidator
import com.harleyoconnor.gitdesk.ui.node.TextField
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.process.logFailure
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class EditBranchController : ViewController<EditBranchController.Context> {

    object Loader: ResourceViewLoader<Context, EditBranchController, VBox>(
        UIResource("/ui/layouts/repository/branches/EditBranch.fxml")
    )

    class Context(val parent: BranchesWindow, val repository: Repository, val branch: Branch): ViewController.Context

    private lateinit var parent: BranchesWindow

    private lateinit var branch: Branch

    @FXML
    private lateinit var nameField: TextField

    @FXML
    private lateinit var upstreamField: UpstreamChoiceBox

    @FXML
    private lateinit var saveButton: Button

    override fun setup(context: Context) {
        this.parent = context.parent
        this.branch = context.branch
        nameField.setText(branch.name)
        nameField.setOrAppendValidator(BranchNameAvailableValidator(context.repository, branch.name))
        upstreamField.loadChoices(context.repository)
        selectCurrentUpstream()
    }

    private fun selectCurrentUpstream() {
        branch.getUpstream()?.let { currentUpstream ->
            upstreamField.select(currentUpstream)
        } ?: upstreamField.selectionModel.selectFirst()
    }

    @FXML
    private fun cancel(event: ActionEvent) {
        parent.openMainView()
    }

    @FXML
    private fun save(event: ActionEvent) {
        val name = nameField.getTextOrNull() ?: return
        upstreamField.selectionModel.selectedItem.get()?.let {
            branch.setUpstream(it).ifFailure(::logFailure).beginAndWaitFor()
        }
        branch.rename(name)
            .ifSuccessful {
                parent.refreshRepositoryWindow()
            }
            .ifFailure(::logFailure).beginAndWaitFor()
        parent.openMainView()
    }

    @FXML
    private fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            saveButton.fire()
        }
    }

}