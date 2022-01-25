package com.harleyoconnor.gitdesk.ui.repository.branch

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
class CreateBranchController : ViewController<CreateBranchController.Context> {

    object Loader: ResourceViewLoader<Context, CreateBranchController, VBox>(
        UIResource("/ui/layouts/repository/branches/CreateBranch.fxml")
    )

    class Context(val parent: BranchesWindow, val repository: Repository): ViewController.Context

    private lateinit var parent: BranchesWindow

    private lateinit var repository: Repository

    @FXML
    private lateinit var baseField: BranchChoiceBox

    @FXML
    private lateinit var nameField: TextField

    @FXML
    private lateinit var upstreamField: UpstreamChoiceBox

    @FXML
    private lateinit var createButton: Button

    override fun setup(context: Context) {
        this.parent = context.parent
        this.repository = context.repository
        baseField.loadChoices(repository)
        baseField.selectionModel.selectFirst()
        nameField.setOrAppendValidator(BranchNameAvailableValidator(repository))
        upstreamField.loadChoices(this.repository)
        upstreamField.selectionModel.selectFirst()
    }

    @FXML
    private fun cancel(event: ActionEvent) {
        parent.openMainView()
    }

    @FXML
    private fun createAndCheckout() {
        val name: String = nameField.getTextOrNull() ?: return
        createBranch(name)
        repository.getBranch(name).checkOut().ifFailure(::logFailure).beginAndWaitFor()
        parent.openMainView()
    }

    @FXML
    private fun create() {
        val name: String = nameField.getTextOrNull() ?: return
        createBranch(name)
        parent.openMainView()
    }

    private fun createBranch(name: String) {
        val selection = upstreamField.selectionModel.selectedItem.get()
        val baseBranch = baseField.selectionModel.selectedItem.get()!!
        if (selection != null) {
            repository.createBranchWithUpstream(baseBranch, name, selection)
                .ifFailure(::logFailure)
                .beginAndWaitFor()
        } else {
            repository.createBranch(baseBranch, name).ifFailure(::logFailure).beginAndWaitFor()
        }
    }

    fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            createButton.fire()
        }
    }

}