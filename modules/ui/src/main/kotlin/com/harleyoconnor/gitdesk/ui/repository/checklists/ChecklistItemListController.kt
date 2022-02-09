package com.harleyoconnor.gitdesk.ui.repository.checklists

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.checklist.ChecklistItem
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.RadioContextMenu
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.node.SelectionCellList
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.util.flip
import com.harleyoconnor.gitdesk.ui.util.thenAcceptOnMainThread
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.map
import com.harleyoconnor.gitdesk.util.sort
import com.harleyoconnor.gitdesk.util.toTypedArray
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

class ChecklistItemListController : ViewController<ChecklistItemListController.Context> {

    object Loader : ResourceViewLoader<Context, ChecklistItemListController, VBox>(
        UIResource("/ui/layouts/repository/checklists/ChecklistItemList.fxml")
    )

    class Context(
        val openItemCallback: (ChecklistItem) -> Unit,
        val checklistContext: ChecklistContext
    ) : ViewController.Context

    private lateinit var openItemCallback: (ChecklistItem) -> Unit
    private lateinit var checklistContext: ChecklistContext

    @FXML
    private lateinit var searchBar: TextField

    private var searchQuery = ""

    private var sort: Sort = Sort.TITLE
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
    private lateinit var content: SelectionCellList<ChecklistItem>

    private val sortContextMenu by lazy {
        RadioContextMenu(
            Sort.values().associateBy {
                TRANSLATIONS_BUNDLE.getString("sort.${it.name.lowercase()}")
            },
            Sort.TITLE
        ) {
            sort = it
        }
    }

    override fun setup(context: Context) {
        openItemCallback = context.openItemCallback
        checklistContext = context.checklistContext

        content.setOnElementSelected {
            context.openItemCallback(it.element)
        }

        registerSearchBarInputs()
        updateSearchResults()
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

    fun updateSearchResults() {
        content.clear()
        loadItems()
    }

    /**
     * Take opportunity of refresh to reload items from server. This should keep us up to relatively up-to-date with any
     * server-side changes.
     */
    private fun loadItems() {
        checklistContext.checklist.getItems(checklistContext.remoteContext.remote)
            .thenApply { items ->
                loadCells(filterAndSortItems(items))
            }
            .thenAcceptOnMainThread {
                displayCells(it)
            }
    }

    private fun filterAndSortItems(items: List<ChecklistItem>): Array<ChecklistItem> {
        return sortItems(
            items.stream()
                .filter { it.title.contains(searchQuery) } // Filter to where title contains query.
                .toTypedArray()
        )
    }

    private fun sortItems(items: Array<ChecklistItem>): Array<ChecklistItem> {
        return sort.sort(items).map {
            if (sortOrder == RemoteRepository.SortOrder.DESCENDING) {
                it.reversedArray()
            } else it
        }
    }

    private fun loadCells(items: Array<ChecklistItem>): Map<ChecklistItem, HBox> {
        return items.associateWith {
            ChecklistItemCellController.Loader.load(
                ChecklistItemCellController.Context(this::select, ChecklistItemContext(checklistContext, it))
            ).root
        }
    }

    private fun select(item: ChecklistItem) {
        content.select(item)
    }

    private fun displayCells(cells: Map<ChecklistItem, HBox>) {
        cells.forEach { cell ->
            content.addElement(cell.key, cell.value)
        }
    }

    enum class Sort(
        private val comparator: Comparator<ChecklistItem>
    ) {
        TITLE({ i1, i2 -> i1.title.compareTo(i2.title) }),
        COMPLETED({ i1, _ -> if (i1.completed) 1 else -1 });

        fun sort(items: Array<ChecklistItem>): Array<ChecklistItem> {
            return sort(items, comparator)
        }
    }

}