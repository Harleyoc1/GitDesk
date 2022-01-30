package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.IssueCellList
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.apache.logging.log4j.LogManager
import org.fxmisc.wellbehaved.event.EventPattern
import org.fxmisc.wellbehaved.event.InputMap
import org.fxmisc.wellbehaved.event.Nodes

/**
 *
 * @author Harley O'Connor
 */
class IssuesListController : ViewController<IssuesListController.Context> {

    object Loader : ResourceViewLoader<Context, IssuesListController, VBox>(
        UIResource("/ui/layouts/repository/issues/IssueList.fxml")
    )

    class Context(val parent: IssuesTabController, val remote: RemoteRepository) : ViewController.Context

    private lateinit var parent: IssuesTabController
    private lateinit var remote: RemoteRepository

    @FXML
    private lateinit var searchBar: TextField

    private var lastQuery = ""

    @FXML
    private lateinit var content: IssueCellList

    override fun setup(context: Context) {
        parent = context.parent
        remote = context.remote
        registerSearchBarInputs()
        updateSearchResults(searchBar.text)
    }

    private fun registerSearchBarInputs() {
        Nodes.addInputMap(searchBar, InputMap.sequence(
            InputMap.consume(EventPattern.keyPressed(KeyCode.ENTER)) {
                updateSearchResults(searchBar.text)
            },
            InputMap.consume(EventPattern.keyPressed(KeyCode.UP)) {
                content.moveSelectionUp()
                content.select()
            },
            InputMap.consume(EventPattern.keyPressed(KeyCode.DOWN)) {
                content.moveSelectionDown()
                content.select()
            }
        ))
    }

    private fun updateSearchResults(query: String) {
        if (query == lastQuery) {
            return
        }
        lastQuery = query
        remote.getIssues(
            query,
            "best match",
            RemoteRepository.Order.DESCENDING,
            Application.getInstance().backgroundExecutor
        ).exceptionally {
            // If it was completed exceptionally, log it and display no issues.
            LogManager.getLogger().error("Error thrown whilst searching for issues:", it)
            return@exceptionally emptyArray<Issue>()
        }.thenAccept {
            val cells = loadCells(it)
            Platform.runLater {
                displayCells(cells)
            }
        }
    }

    private fun loadCells(issues: Array<Issue>): Map<Issue, HBox> {
        return issues.map {
            it to IssueCellController.Loader.load(IssueCellController.Context(this, it)).root
        }.toMap()
    }

    private fun displayCells(cells: Map<Issue, HBox>) {
        content.clear()
        cells.forEach { cell ->
            content.addElement(cell.key, cell.value)
        }
    }

}