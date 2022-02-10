package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.ui.node.RadioContextMenu
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.node.SelectionCellList
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.util.flip
import com.harleyoconnor.gitdesk.ui.util.whenScrolledToBottom
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.Side
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import org.fxmisc.wellbehaved.event.EventPattern
import org.fxmisc.wellbehaved.event.InputMap
import org.fxmisc.wellbehaved.event.Nodes

/**
 * Abstract implementation of an issue cell list view controller, used by both issue and PR cell lists.
 *
 * @author Harley O'Connor
 */
abstract class AbstractIssuesListController<I : Issue, C : AbstractIssuesListController.Context<I>> : ViewController<C> {

    open class Context<I : Issue>(
        val selectCallback: (I) -> Unit,
        val remote: RemoteContext
    ) : ViewController.Context

    private lateinit var selectCallback: (I) -> Unit
    protected lateinit var remoteContext: RemoteContext

    @FXML
    private lateinit var searchBar: TextField

    protected var searchQuery = ""

    protected var sort: Issue.Sort = Issue.Sort.BEST_MATCH
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
            Issue.Sort.values().associateBy {
                TRANSLATIONS_BUNDLE.getString("sort.${it.name.lowercase()}")
            },
            Issue.Sort.BEST_MATCH
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
        selectCallback = context.selectCallback
        remoteContext = context.remote

        content.setOnElementSelected {
            selectCallback(it.element)
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
        sortOrderIcon.flip()
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