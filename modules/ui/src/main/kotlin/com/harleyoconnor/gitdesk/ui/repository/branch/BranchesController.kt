package com.harleyoconnor.gitdesk.ui.repository.branch

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.git.repository.Branch
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.BranchCellList
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.process.logFailure
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.fxmisc.wellbehaved.event.EventPattern
import org.fxmisc.wellbehaved.event.InputMap
import org.fxmisc.wellbehaved.event.Nodes

/**
 *
 * @author Harley O'Connor
 */
class BranchesController : ViewController<BranchesController.Context> {

    object Loader: ResourceViewLoader<Context, BranchesController, VBox>(
        UIResource("/ui/layouts/repository/branches/Branches.fxml")
    )

    class Context(val parent: BranchesWindow, val repository: LocalRepository): ViewController.Context

    private lateinit var parent: BranchesWindow

    private lateinit var repository: LocalRepository

    private lateinit var branches: Array<Branch>

    @FXML
    private lateinit var searchBar: TextField
    private var lastSearchQuery = ""

    @FXML
    private lateinit var content: BranchCellList

    private val cellsCache: MutableMap<Branch, HBox> = mutableMapOf()

    @FXML
    private fun initialize() {
        Nodes.addInputMap(searchBar, InputMap.sequence(
            InputMap.consume(EventPattern.keyPressed(KeyCode.UP)) {
                content.moveSelectionUp()
            },
            InputMap.consume(EventPattern.keyPressed(KeyCode.DOWN)) {
                content.moveSelectionDown()
            },
            InputMap.consume(EventPattern.keyPressed(KeyCode.ENTER)) {
                content.select()
            }
        ))
        content.setOnElementSelected { event ->
            checkOutBranch(event.element)
            closeWindow()
        }
    }

    override fun setup(context: Context) {
        this.parent = context.parent
        this.repository = context.repository
        repository.gitRepository.getAllBranches()
            .ifSuccessful { response ->
                Platform.runLater {
                    response.result?.let { branches = it }
                    displayBranches("")
                }
            }
            .ifFailure(::logFailure)
            .begin()
    }

    fun checkOutBranch(branch: Branch) {
        branch.checkOut()
            .ifSuccessful {
                Platform.runLater {
                    parent.refreshRepositoryWindow()
                }
            }
            .ifFailure(::logFailure)
            .begin()
    }

    fun removeBranchCell(branch: Branch) {
        cellsCache[branch]?.let { content.removeElement(branch, it) }
    }

    fun closeWindow() {
        parent.close()
    }

    fun openEditBranchView(branch: Branch) {
        parent.openEditView(branch)
    }

    @FXML
    private fun onSearchQueryUpdated(event: KeyEvent) {
        val query = searchBar.text
        if (lastSearchQuery == query) {
            return
        }
        lastSearchQuery = query
        clearDisplayedBranches()
        displayBranches(query)
    }

    private fun clearDisplayedBranches() {
        this.content.clear()
    }

    private fun displayBranches(searchQuery: String) {
        branches.forEach {
            if (searchQuery.isEmpty() || matchesQuery(it.name, searchQuery)) {
                displayBranch(it)
            }
        }
    }

    @Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")
    private fun displayBranch(branch: Branch) {
        content.addElement(branch, cellsCache.computeIfAbsent(branch) {
            BranchCellController.Loader.load(BranchCellController.Context(this, it)).root
        })
    }

    @FXML
    private fun toAddBranchView(event: ActionEvent) {
        parent.openAddView()
    }

    private fun matchesQuery(name: String, query: String) = name.lowercase().contains(query.lowercase())

}