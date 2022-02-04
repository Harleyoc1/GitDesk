package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.IssueCellList
import com.harleyoconnor.gitdesk.ui.node.RadioContextMenu
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.util.exceptionallyOnMainThread
import com.harleyoconnor.gitdesk.ui.util.logErrorAndCreateDialogue
import com.harleyoconnor.gitdesk.ui.util.thenAcceptOnMainThread
import com.harleyoconnor.gitdesk.ui.util.whenScrolledToBottom
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.Side
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
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

    class Context(val parent: IssuesTabController, val remote: RemoteContext) : ViewController.Context

    private lateinit var parent: IssuesTabController
    private lateinit var remoteContext: RemoteContext

    @FXML
    private lateinit var searchBar: TextField

    private var lastQuery = ""

    private var sort: RemoteRepository.Sort = RemoteRepository.Sort.BEST_MATCH
        set(value) {
            field = value; updateSearchResults()
        }

    private var sortOrder: RemoteRepository.SortOrder = RemoteRepository.SortOrder.DESCENDING
        set(value) {
            field = value; updateSearchResults()
        }

    @FXML
    private lateinit var searchOptionsBox: HBox

    @FXML
    private lateinit var sortOrderIcon: SVGIcon

    @FXML
    private lateinit var contentScrollPane: ScrollPane

    @FXML
    private lateinit var content: IssueCellList

    private var nextIssuePage: Int = 1

    private val sortContextMenu by lazy {
        RadioContextMenu(
            RemoteRepository.Sort.values().associateBy {
                TRANSLATIONS_BUNDLE.getString("sort.${it.name.lowercase()}")
            },
            RemoteRepository.Sort.BEST_MATCH
        ) {
            sort = it
        }
    }

    @FXML
    private fun initialize() {
        contentScrollPane.whenScrolledToBottom {
            loadNextIssuePage()
        }
    }

    override fun setup(context: Context) {
        parent = context.parent
        remoteContext = context.remote

        content.setOnElementSelected {
            parent.setShownIssue(it.element)
        }

        registerSearchBarInputs()
        updateSearchResults(searchBar.text)
    }

    private fun registerSearchBarInputs() {
        Nodes.addInputMap(searchBar, InputMap.sequence(
            InputMap.consume(EventPattern.keyPressed(KeyCode.ENTER)) {
                if (!updateSearchResults(searchBar.text)) {
                    content.select()
                }
            },
            InputMap.consume(EventPattern.keyPressed(KeyCode.UP)) {
                content.moveSelectionUp()
            },
            InputMap.consume(EventPattern.keyPressed(KeyCode.DOWN)) {
                content.moveSelectionDown()
            }
        ))
    }

    @FXML
    private fun openSortMenu(event: ActionEvent) {
        sortContextMenu.show(searchOptionsBox, Side.BOTTOM, 0.0, 0.0)
    }

    @FXML
    private fun toggleSortOrder(event: ActionEvent) {
        sortOrder = sortOrder.other()
        flipSortOrderIcon()
    }

    private fun flipSortOrderIcon() {
        sortOrderIcon.rotate = (sortOrderIcon.rotate + 180) % 360
    }

    /**
     * @return `true` if a search will be completed
     */
    private fun updateSearchResults(query: String): Boolean {
        if (query == lastQuery) {
            return false
        }
        lastQuery = query
        updateSearchResults()
        return true
    }

    private fun updateSearchResults() {
        nextIssuePage = 1
        content.clear()
        loadNextIssuePage()
    }

    private fun loadNextIssuePage() {
        remoteContext.remote.getIssues(
            lastQuery,
            sort,
            sortOrder,
            nextIssuePage++,
            Application.getInstance().backgroundExecutor
        ).thenApply {
            loadCells(it)
        }.thenAcceptOnMainThread { cells ->
            displayCells(cells)
        }.exceptionallyOnMainThread {
            logErrorAndCreateDialogue("dialogue.error.searching_issues", it)
        }
    }

    private fun loadCells(issues: Array<Issue>): Map<Issue, HBox> {
        return issues.associateWith {
            IssueCellController.Loader.load(IssueCellController.Context(this, it)).root
        }
    }

    private fun displayCells(cells: Map<Issue, HBox>) {
        cells.forEach { cell ->
            content.addElement(cell.key, cell.value)
        }
    }

    fun select(issue: Issue) {
        content.select(issue)
    }

}