package com.harleyoconnor.gitdesk.ui.repository.editor

import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.settings.AppSettings
import com.harleyoconnor.gitdesk.ui.style.OPEN_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.style.SELECTED_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
import com.harleyoconnor.gitdesk.ui.util.getIcon
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.system.SystemManager
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import java.io.File

/**
 * @author Harley O'Connor
 */
open class FileCellController<C : FileCellController.Context> : ViewController<C> {

    object Loader: ResourceViewLoader<Context, FileCellController<Context>, HBox>(
        UIResource("/ui/layouts/repository/editor/FileCell.fxml")
    )

    open class Context(val parent: FileListController, val file: File, val insetIndex: Int): ViewController.Context

    lateinit var file: File
    protected lateinit var parent: FileListController

    private val parentDirectory by lazy {
        Directory(file.parentFile)
    }

    @FXML
    private lateinit var cell: HBox

    @FXML
    private lateinit var contextMenu: ContextMenu

    @FXML
    private lateinit var openInMenu: Menu

    @FXML
    private lateinit var openInExternalEditorMenuItem: MenuItem

    @FXML
    private lateinit var icon: SVGIcon

    @FXML
    private lateinit var nameLabel: Label

    override fun setup(context: C) {
        this.file = context.file
        this.parent = context.parent
        cell.padding = getPadding(context.insetIndex)
        icon.setupFromSvg(file.getIcon())
        nameLabel.text = file.name
    }

    protected open fun getPadding(insetIndex: Int): Insets =
        Insets(2.0, 8.0, 2.0, 23.0 + insetIndex * 15.0)

    @FXML
    private fun select(event: MouseEvent) {
        if (event.button == MouseButton.SECONDARY || event.isControlDown) {
            openContextMenu(event)
        } else if (cell.pseudoClassStates.contains(SELECTED_PSEUDO_CLASS)) {
            open(event)
            return
        }
        parent.selected = this
        cell.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, true)
    }

    private fun openContextMenu(event: MouseEvent) {
        setupOpenInExternalEditorMenuItem()
        contextMenu.show(cell, event.screenX, event.screenY)
    }

    private fun setupOpenInExternalEditorMenuItem() {
        addOpenInExternalEditorMenuItem() // Add item in case the config option has been changed from unset.
        getExternalEditor()?.let { appFile ->
            openInExternalEditorMenuItem.text = appFile.nameWithoutExtension
        } ?: run {
            removeOpenInExternalEditorMenuItem() // Remove item if the config option is not set.
        }
    }

    private fun addOpenInExternalEditorMenuItem() {
        if (openInMenu.items.size < 2) {
            openInMenu.items.add(openInExternalEditorMenuItem)
        }
    }

    protected fun removeOpenInExternalEditorMenuItem() {
        openInMenu.items.remove(openInExternalEditorMenuItem)
    }

    fun deselect() {
        cell.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, false)
    }

    open fun open(event: Event) {
        parent.open = this
        cell.pseudoClassStateChanged(OPEN_PSEUDO_CLASS, true)
    }

    fun displayAsClosed() {
        cell.pseudoClassStateChanged(OPEN_PSEUDO_CLASS, false)
    }

    @FXML
    private fun createNewFile(event: ActionEvent) {
        CreateFileWindow(parentDirectory) {
            parent.reloadCellsFor(parentDirectory)
        }.open()
    }

    @FXML
    private fun createNewDirectory(event: ActionEvent) {
        CreateDirectoryWindow(parentDirectory) {
            parent.reloadCellsFor(parentDirectory)
        }.open()
    }

    @FXML
    private fun delete(event: ActionEvent) {
        createDeleteFileDialogue()
            .showAndWait()
            .filter(ButtonType.OK::equals)
            .ifPresent {
                file.delete()
                parent.onFileDeleted(file)
            }
    }

    protected fun createDeleteFileDialogue(): Alert {
        val alert = Alert(Alert.AlertType.CONFIRMATION)
        alert.headerText = TRANSLATIONS_BUNDLE.getString("dialogue.confirm.file_deletion.header")
        alert.headerText = TRANSLATIONS_BUNDLE.getString("dialogue.confirm.file_deletion.content", file.name)
        return alert
    }

    @FXML
    private fun openInFileBrowser(event: ActionEvent) {
        SystemManager.get().openInFileBrowser(file).begin()
    }

    @FXML
    private fun openInExternalEditor(event: ActionEvent) {
        getExternalEditor()?.let { appFile ->
            SystemManager.get().openInApp(file, appFile).begin()
        }
    }

    private fun getExternalEditor() = AppSettings.get().getOrLoad().integrations.defaultExternalEditor

}
