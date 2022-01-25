package com.harleyoconnor.gitdesk.ui.repository.branch

import com.harleyoconnor.gitdesk.git.repository.Branch
import com.harleyoconnor.gitdesk.git.repository.RemoteBranch
import com.harleyoconnor.gitdesk.git.repository.Repository
import com.harleyoconnor.gitdesk.ui.form.validation.BranchNameAvailableValidator
import com.harleyoconnor.gitdesk.ui.node.TextField
import com.harleyoconnor.gitdesk.ui.util.load
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
class EditBranchController {

    companion object {
        fun load(parent: BranchesWindow, repository: Repository, branch: Branch): VBox {
            val fxml = load<VBox, EditBranchController>("repository/branches/EditBranch")
            fxml.controller.setup(parent, repository, branch)
            return fxml.root
        }
    }

    private lateinit var parent: BranchesWindow

    private lateinit var branch: Branch

    @FXML
    private lateinit var nameField: TextField

    @FXML
    private lateinit var upstreamField: UpstreamChoiceBox

    @FXML
    private lateinit var saveButton: Button

    private fun setup(parent: BranchesWindow, repository: Repository, branch: Branch) {
        this.parent = parent
        this.branch = branch
        nameField.setText(branch.name)
        nameField.setOrAppendValidator(BranchNameAvailableValidator(repository, branch.name))
        upstreamField.loadChoices(repository)
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