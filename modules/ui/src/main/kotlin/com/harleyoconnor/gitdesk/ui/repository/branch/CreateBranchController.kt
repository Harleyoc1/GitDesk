package com.harleyoconnor.gitdesk.ui.repository.branch

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.git.repository.Branch
import com.harleyoconnor.gitdesk.git.repository.RemoteBranch
import com.harleyoconnor.gitdesk.git.repository.Repository
import com.harleyoconnor.gitdesk.ui.node.TextField
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.ui.validation.BranchNameAvailableValidator
import com.harleyoconnor.gitdesk.ui.validation.FieldValidator
import com.harleyoconnor.gitdesk.util.process.logFailure
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Separator
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import java.lang.Exception

/**
 * @author Harley O'Connor
 */
class CreateBranchController {

    companion object {
        fun load(parent: BranchesWindow, repository: LocalRepository): VBox {
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
    private lateinit var upstreamField: ChoiceBox<Any>

    @FXML
    private lateinit var createButton: Button

    private val bases: MutableMap<String, Branch> = mutableMapOf()
    private val upstreams: MutableMap<String, RemoteBranch> = mutableMapOf()

    private fun setup(parent: BranchesWindow, repository: LocalRepository) {
        this.parent = parent
        this.repository = repository.gitRepository
        repository.gitRepository.getAllBranches().ifSuccessful {
            it.result?.let { branches ->
                Platform.runLater {
                    populateBaseField(branches)
                }
            }
        }.ifFailure(::logFailure).begin()
        repository.gitRepository.getRemoteBranches().ifSuccessful {
            it.result?.let { remoteBranches ->
                Platform.runLater {
                    populateUpstreamField(remoteBranches)
                }
            }
        }.ifFailure(::logFailure).begin()
        nameField.setOrAppendValidator(BranchNameAvailableValidator(repository.gitRepository))
    }

    private fun populateBaseField(branches: Array<Branch>) {
        var currentBranchName: String? = null
        baseField.items = FXCollections.observableArrayList(
            branches.map {
                if (it.checkedOut) {
                    currentBranchName = it.name
                }
                bases[it.name] = it
                return@map it.name
            }
        )
        baseField.selectionModel.select(currentBranchName)
    }

    private fun populateUpstreamField(remoteBranches: Array<RemoteBranch>) {
        upstreamField.items = FXCollections.observableArrayList(
            TRANSLATIONS_BUNDLE.getString("ui.repository.branches.create.field.upstream.none"),
            Separator(),
            *remoteBranches.map { remoteBranch ->
                val upstreamId = remoteBranch.remote.name + "/" + remoteBranch.name
                upstreams[upstreamId] = remoteBranch
                upstreamId
            }.toTypedArray()
        )
        upstreamField.selectionModel.selectFirst()
    }

    @FXML
    private fun cancel(event: ActionEvent) {
        parent.openMainView()
    }

    @FXML
    private fun createAndCheckout() {
        val name: String = getNameOrNull() ?: return
        createBranch(name)
        repository.getBranch(name).checkOut().ifFailure(::logFailure).beginAndWaitFor()
        parent.openMainView()
    }

    @FXML
    private fun create() {
        val name: String = getNameOrNull() ?: return
        createBranch(name)
        parent.openMainView()
    }

    private fun createBranch(name: String) {
        val upstream = getUpstreamSelection()
        if (upstream != null) {
            repository.createBranchWithUpstream(getBaseSelection(), name, upstream).ifFailure(::logFailure)
                .beginAndWaitFor()
        } else {
            repository.createBranch(getBaseSelection(), name).ifFailure(::logFailure).beginAndWaitFor()
        }
    }

    private fun getNameOrNull(): String? {
        return try {
            nameField.getText()
        } catch (e: FieldValidator.InvalidException) {
            null
        }
    }

    private fun getBaseSelection(): Branch {
        val branchName = baseField.selectionModel.selectedItem
        return bases[branchName]!!
    }

    private fun getUpstreamSelection(): RemoteBranch? {
        val upstreamSelection = upstreamField.selectionModel.selectedItem
        if (upstreamSelection is String) {
            return upstreams[upstreamSelection]
        }
        return null
    }

    fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            createButton.fire()
        }
    }
}