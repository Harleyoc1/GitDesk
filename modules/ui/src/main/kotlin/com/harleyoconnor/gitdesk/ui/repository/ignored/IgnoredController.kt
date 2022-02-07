package com.harleyoconnor.gitdesk.ui.repository.ignored

import com.harleyoconnor.gitdesk.git.repository.IgnoreFile
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.RuleCellList
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.Side
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
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
class IgnoredController : ViewController<IgnoredController.Context> {

    object Loader : ResourceViewLoader<Context, IgnoredController, VBox>(
        UIResource("/ui/layouts/repository/ignored/Ignored.fxml")
    )

    class Context(
        val ignoreFile: IgnoreFile,
        val openAddViewCallback: (IgnoreFile.Rule) -> Unit,
        val openEditViewCallback: (IgnoreFile.Rule) -> Unit
    ) : ViewController.Context

    private lateinit var ignoredFile: IgnoreFile
    private lateinit var openAddViewCallback: (IgnoreFile.Rule) -> Unit
    private lateinit var openEditViewCallback: (IgnoreFile.Rule) -> Unit

    @FXML
    private lateinit var ruleTypeSelectionMenu: ContextMenu

    @FXML
    private lateinit var searchBar: TextField

    private var lastSearchQuery = ""

    @FXML
    private lateinit var addButton: Button

    @FXML
    private lateinit var content: RuleCellList

    private val cellsCache: MutableMap<IgnoreFile.Rule, HBox> = mutableMapOf()

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
            openEditView(event.element)
        }
    }

    override fun setup(context: Context) {
        ignoredFile = context.ignoreFile
        openAddViewCallback = context.openAddViewCallback
        openEditViewCallback = context.openEditViewCallback

        displayRules("")
    }

    @FXML
    private fun openAddView(event: ActionEvent) {
        ruleTypeSelectionMenu.show(addButton, Side.BOTTOM, 0.0, 0.0)
    }

    @FXML
    private fun openAddDirectoryRuleView(event: ActionEvent) {
        openAddView(IgnoreFile.DirectoryRule("<path>/"))
    }

    @FXML
    private fun openAddFileExtensionRuleView(event: ActionEvent) {
        openAddView(IgnoreFile.FileExtensionRule("*.<extension>"))
    }

    @FXML
    private fun openAddCustomRuleView(event: ActionEvent) {
        openAddView(IgnoreFile.CustomRule(""))
    }

    private fun openAddView(template: IgnoreFile.Rule) {
        openAddViewCallback(template)
    }

    private fun openEditView(rule: IgnoreFile.Rule) {
        openEditViewCallback(rule)
    }

    private fun delete(rule: IgnoreFile.Rule) {
        ignoredFile.remove(rule)
        cellsCache.remove(rule)?.let { node ->
            content.removeElement(rule, node)
        }
    }

    @FXML
    private fun onSearchQueryUpdated(event: KeyEvent) {
        val query = searchBar.text
        if (lastSearchQuery == query) {
            return
        }
        lastSearchQuery = query
        content.clear()
        displayRules(query)
    }

    private fun displayRules(searchQuery: String) {
        ignoredFile.rules.forEach {
            if (searchQuery.isEmpty() || matchesQuery(it.body, searchQuery)) {
                displayRule(it)
            }
        }
    }

    @Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")
    private fun displayRule(rule: IgnoreFile.Rule) {
        content.addElement(rule, cellsCache.computeIfAbsent(rule) {
            RuleCellController.load(it, this::openEditView, this::delete).root
        })
    }

    private fun matchesQuery(name: String, query: String) = name.lowercase().contains(query.lowercase())

}