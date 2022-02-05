package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.RadioContextMenu
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.node.SelectionCellList
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
abstract class AbstractIssuesListController<I : Issue, C : AbstractIssuesListController.Context<I>> : ViewController<C> {

    class Issues : AbstractIssuesListController<Issue, Issues.Context>() {

        object Loader : ResourceViewLoader<Context, Issues, VBox>(
            UIResource("/ui/layouts/repository/issues/IssueList.fxml")
        )

        open class Context(
            openIssueCallback: (Issue) -> Unit,
            remote: RemoteContext
        ) : AbstractIssuesListController.Context<Issue>(openIssueCallback, remote)

        override fun loadPage(page: Int) {
            remoteContext.remote.getIssues(
                searchQuery,
                sort,
                sortOrder,
                page,
                Application.getInstance().backgroundExecutor
            ).thenApply {
                loadCells(it)
            }.thenAcceptOnMainThread { cells ->
                displayCells(cells)
            }.exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.searching_issues", it).show()
            }
        }

        private fun loadCells(issues: Array<out Issue>): Map<Issue, HBox> {
            return issues.associateWith {
                IssueCellController.Loader.load(IssueCellController.Context(this::select, it)).root
            }
        }

    }

    open class Context<I : Issue>(
        val openIssueCallback: (I) -> Unit,
        val remote: RemoteContext
    ) : ViewController.Context

    private lateinit var openIssueCallback: (I) -> Unit
    protected lateinit var remoteContext: RemoteContext

    @FXML
    private lateinit var searchBar: TextField

    protected var searchQuery = ""

    protected var sort: RemoteRepository.Sort = RemoteRepository.Sort.BEST_MATCH
        set(value) {
            field = value; updateSearchResults()
        }

    protected var sortOrder: RemoteRepository.SortOrder = RemoteRepository.SortOrder.DESCENDING
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
    private lateinit var content: SelectionCellList<I>

    private var nextPage: Int = 1

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
            loadNextPage()
        }
    }

    override fun setup(context: C) {
        openIssueCallback = context.openIssueCallback
        remoteContext = context.remote

        content.setOnElementSelected {
            openIssueCallback(it.element)
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
        if (query == this.searchQuery) {
            return false
        }
        this.searchQuery = query
        updateSearchResults()
        return true
    }

    private fun updateSearchResults() {
        nextPage = 1
        content.clear()
        loadNextPage()
    }

    private fun loadNextPage() =
        loadPage(nextPage++)

    protected abstract fun loadPage(page: Int)

    protected fun displayCells(cells: Map<I, HBox>) {
        cells.forEach { cell ->
            content.addElement(cell.key, cell.value)
        }
    }

    fun select(issue: I) {
        content.select(issue)
    }

}