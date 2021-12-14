package com.harleyoconnor.gitdesk.ui.menu.open

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.util.getUserHome
import com.harleyoconnor.gitdesk.util.system.SystemManager
import javafx.event.Event
import javafx.fxml.FXML
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.HBox

/**
 *
 * @author Harley O'Connor
 */
class RepositoryCellController {

    companion object {
        fun loadCell(repository: LocalRepository): HBox {
            val fxml = load<HBox, RepositoryCellController>("menu/tabs/open/RepositoryCell")
            fxml.controller.setRepository(repository)
            return fxml.root
        }
    }

    @FXML
    private lateinit var root: HBox

    @FXML
    private lateinit var contextMenu: ContextMenu

    @FXML
    private lateinit var label: Label

    @FXML
    private lateinit var pathLabel: Label

    @FXML
    lateinit var remoteLabel: Label

    @FXML
    lateinit var remotePlatformIcon: SVGIcon

    private lateinit var repository: LocalRepository

    @FXML
    fun initialize() {
        root.setOnContextMenuRequested {
            contextMenu.show(root, it.screenX, it.screenY)
        }
    }

    private fun setRepository(repository: LocalRepository) {
        this.repository = repository
        label.text = repository.id
        updateUiWithPath(repository.directory.path)
        // TODO: Display remote data
    }

    private fun updateUiWithPath(path: String) {
        // Make path in cell more concise.
        val formattedPath = path.replace(getUserHome(), "~")
            .replace("Library/Mobile Documents/com~apple~CloudDocs", "iCloud")
        pathLabel.text = formattedPath

        // Display full path in tooltip.
        pathLabel.tooltip = Tooltip(path)
    }

    @FXML
    fun openRepository(event: Event) {

    }

    @FXML
    fun openInFileBrowser() {
        SystemManager.get().openInFileBrowser(repository.directory).begin()
    }

}