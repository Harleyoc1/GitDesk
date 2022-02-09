package com.harleyoconnor.gitdesk.ui.repository.checklists

import com.harleyoconnor.gitdesk.data.remote.checklist.Checklist
import com.harleyoconnor.gitdesk.data.remote.checklist.ChecklistItem
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.ui.view.ViewLoader
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class ChecklistsTabController : ViewController<ChecklistsTabController.Context> {

    object Loader : ResourceViewLoader<Context, ChecklistsTabController, SplitPane>(
        UIResource("/ui/layouts/repository/checklists/ChecklistsTab.fxml")
    )

    class Context(val remoteContext: RemoteContext) :
        ViewController.Context

    private lateinit var remoteContext: RemoteContext

    private var currentChecklist: Checklist? = null
    private var itemListView: ViewLoader.View<ChecklistItemListController, out Node>? = null

    @FXML
    private lateinit var root: SplitPane

    @FXML
    private lateinit var sideBar: VBox

    @FXML
    private lateinit var toolBarBox: HBox

    @FXML
    private lateinit var titleLabel: Label

    @FXML
    private lateinit var createItemButton: Button

    @FXML
    private lateinit var checklistsBox: HBox

    @FXML
    private lateinit var checklistField: ChecklistChoiceBox

    @FXML
    private lateinit var createButton: Button

    override fun setup(context: Context) {
        remoteContext = context.remoteContext
        titleLabel.text = context.remoteContext.remote.name.getFullName()
        setupToolbar()
        setupChecklistField()
    }

    private fun setupToolbar() {
        // Non-collaborators cannot create checklist items.
        if (!remoteContext.loggedInUserIsCollaborator) {
            toolBarBox.children.remove(createItemButton)
        }
    }

    private fun setupChecklistField() {
        checklistField.selectionModel.selectedItemProperty().addListener { _, _, new ->
            new.get()?.let {
                loadForChecklist(it)
            }
        }
        checklistField.loadChoices(remoteContext.remote)
        // Non-collaborators cannot create checklists.
        if (!remoteContext.loggedInUserIsCollaborator) {
            checklistsBox.children.remove(createButton)
        }
    }

    private fun loadForChecklist(checklist: Checklist) {
        currentChecklist = checklist
        sideBar.children[2] = loadChecklistItemList(checklist)
    }

    private fun loadChecklistItemList(checklist: Checklist): Node {
        itemListView = ChecklistItemListController.Loader.load(
            ChecklistItemListController.Context(this::setShownItem, ChecklistContext(remoteContext, checklist))
        )
        return itemListView!!.root
    }

    private fun setShownItem(checklistItem: ChecklistItem) {
        root.items[1] = ChecklistItemViewController.Loader.load(
            ChecklistItemViewController.Context(
                ChecklistItemContext(ChecklistContext(remoteContext, currentChecklist!!), checklistItem),
                this::removeShownItem
            )
        ).root
    }

    private fun removeShownItem() {
        root.items[1] = VBox()
        itemListView?.controller?.updateSearchResults()
    }

    @FXML
    private fun create(event: ActionEvent) {
        CreateChecklistWindow(remoteContext) {
            checklistField.addChoice(it)
            checklistField.select(it)
        }.open()
    }

    @FXML
    private fun createItem(event: ActionEvent) {
        currentChecklist?.let { checklist ->
            CreateChecklistItemWindow(ChecklistContext(remoteContext, checklist)) { item ->
                setShownItem(item)
                itemListView?.controller?.updateSearchResults()
            }.open()
        }
    }

}