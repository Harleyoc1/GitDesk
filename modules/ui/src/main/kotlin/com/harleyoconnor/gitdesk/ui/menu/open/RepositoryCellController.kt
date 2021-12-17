package com.harleyoconnor.gitdesk.ui.menu.open

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.menu.MenuController
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.repository.RepositoryWindow
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.util.getUserHome
import com.harleyoconnor.gitdesk.util.system.SystemManager
import javafx.fxml.FXML
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox

/**
 *
 * @author Harley O'Connor
 */
class RepositoryCellController {

    companion object {
        fun loadCell(menuController: MenuController, repository: LocalRepository): HBox {
            val fxml = load<HBox, RepositoryCellController>("menu/tabs/open/RepositoryCell")
            fxml.controller.setup(menuController, repository)
            return fxml.root
        }
    }

    private lateinit var menuController: MenuController

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

    private fun setup(menuController: MenuController, repository: LocalRepository) {
        this.menuController = menuController
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
    private fun onCellPressed(event: MouseEvent) {
        if (event.button == MouseButton.PRIMARY) {
            openRepository()
        }
    }

    @FXML
    private fun openRepository() {
        menuController.openRepository(repository)
    }

    @FXML
    private fun openInFileBrowser() {
        SystemManager.get().openInFileBrowser(repository.directory).begin()
    }

}