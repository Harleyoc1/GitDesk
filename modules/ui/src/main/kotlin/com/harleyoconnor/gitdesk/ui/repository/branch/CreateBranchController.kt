package com.harleyoconnor.gitdesk.ui.repository.branch

import com.harleyoconnor.gitdesk.git.repository.Branch
import com.harleyoconnor.gitdesk.git.repository.Repository
import com.harleyoconnor.gitdesk.ui.form.validation.BranchNameAvailableValidator
import com.harleyoconnor.gitdesk.ui.node.TextField
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.util.process.logFailure
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class CreateBranchController {

    companion object {
        fun load(parent: BranchesWindow, repository: Repository): VBox {
            val fxml = load<VBox, CreateBranchController>("repository/branches/CreateBranch")
            fxml.controller.setup(parent, repository)
            return fxml.root
        }
    }

    private lateinit var parent: BranchesWindow

    private lateinit var repository: Repository

    @FXML
    private lateinit var baseField: ChoiceBox<String>

    @FXML
    private lateinit var nameField: TextField

    @FXML
    private lateinit var upstreamField: UpstreamChoiceBox

    @FXML
    private lateinit var createButton: Button

    private val bases: MutableMap<String, Branch> = mutableMapOf()

    private fun setup(parent: BranchesWindow, repository: Repository) {
        this.parent = parent
        this.repository = repository
        repository.getAllBranches().ifSuccessful {
            it.result?.let { branches ->
                Platform.runLater {
                    populateBaseField(branches)
                }
            }
        }.ifFailure(::logFailure).begin()
        upstreamField.loadChoices(this.repository)
        upstreamField.selectionModel.selectFirst()
        nameField.setOrAppendValidator(BranchNameAvailableValidator(repository))
    }

    private fun populateBaseField(branches: Array<Branch>) {
        var currentBranchName: String? = null
        baseField.items = FXCollections.observableArrayList(
            branches.map {
                if (it.isCheckedOut()) {
                    currentBranchName = it.name
                }
                bases[it.name] = it
                return@map it.name
            }
        )
        baseField.selectionModel.select(currentBranchName)
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
        if (selection != null) {
            repository.createBranchWithUpstream(getBaseSelection(), name, selection)
                .ifFailure(::logFailure)
                .beginAndWaitFor()
        } else {
            repository.createBranch(getBaseSelection(), name).ifFailure(::logFailure).beginAndWaitFor()
        }
    }

    private fun getBaseSelection(): Branch {
        val branchName = baseField.selectionModel.selectedItem
        return bases[branchName]!!
    }

    fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            createButton.fire()
        }
    }

}