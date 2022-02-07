package com.harleyoconnor.gitdesk.ui.menu.open

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.data.remote.Platform.GITHUB
import com.harleyoconnor.gitdesk.data.remote.RemoteRepositoryReference
import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.menu.MenuController
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.util.getIcon
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.getUserHome
import com.harleyoconnor.gitdesk.util.system.SystemManager
import com.harleyoconnor.gitdesk.util.xml.SVGCache
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.control.Tooltip
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import java.util.concurrent.CompletableFuture

/**
 *
 * @author Harley O'Connor
 */
class RepositoryCellController : ViewController<RepositoryCellController.Context> {

    object Loader: ResourceViewLoader<Context, RepositoryCellController, HBox>(
        UIResource("/ui/layouts/menu/open/RepositoryCell.fxml")
    )

    class Context(val menuView: MenuController, val repository: LocalRepository): ViewController.Context

    private lateinit var menuView: MenuController

    private lateinit var repository: LocalRepository

    @FXML
    private lateinit var root: HBox

    @FXML
    private lateinit var contextMenu: ContextMenu

    @FXML
    private lateinit var openInBrowserItem: MenuItem

    @FXML
    private lateinit var label: Label

    @FXML
    private lateinit var pathLabel: Label

    @FXML
    lateinit var remoteLabel: Label

    @FXML
    lateinit var remotePlatformIcon: SVGIcon

    private var remote: Remote? = null

    @FXML
    fun initialize() {
        root.setOnContextMenuRequested {
            contextMenu.show(root, it.screenX, it.screenY)
        }
    }

    override fun setup(context: Context) {
        this.menuView = context.menuView
        this.repository = context.repository
        label.text = repository.id
        updateUiWithPath(repository.directory.path)
        queueUpdateUiWithRemoteTask(repository)
    }

    private fun updateUiWithPath(path: String) {
        // Make path in cell more concise.
        val formattedPath = path.replace(getUserHome(), "~")
            .replace("Library/Mobile Documents/com~apple~CloudDocs", "iCloud")
        pathLabel.text = formattedPath

        // Display full path in tooltip.
        pathLabel.tooltip = Tooltip(path)
    }

    private fun queueUpdateUiWithRemoteTask(repository: LocalRepository) {
        CompletableFuture.supplyAsync({
            repository.gitRepository.getCurrentBranch().getUpstream()?.remote
        }, Application.getInstance().backgroundExecutor)
            .thenAccept {
                queueUpdateUiForRemote(it?.remote)
            }
    }

    private fun queueUpdateUiForRemote(remote: Remote?) {
        if (remote == null) {
            Platform.runLater {
                updateUiWithNoRemote()
            }
        } else {
            Platform.runLater {
                updateUiWithRemote(remote)
            }
        }
    }

    private fun updateUiWithRemote(remote: Remote) {
        this.remote = remote
        updateRemoteLabelAndIcon(remote)
        openInBrowserItem.isDisable = false
        openInBrowserItem.setOnAction { openInBrowser() }
    }

    private fun updateRemoteLabelAndIcon(remote: Remote) {
        if (remote is RemoteRepositoryReference) {
            remoteLabel.text = remote.name.getFullName()
            if (remote.platform == GITHUB) {
                remotePlatformIcon.setupFromSvg(remote.platform.getIcon())
            }
        } else {
            remoteLabel.text = remote.url.toExternalForm().substringAfter('@')
                .removePrefix("https://").removeSuffix(".git")
            remotePlatformIcon.setupFromSvg(SVGCache.getOrLoad(UIResource("/ui/icons/web.svg")))
        }
    }

    private fun updateUiWithNoRemote() {
        remote = null
        remoteLabel.text = TRANSLATIONS_BUNDLE.getString("ui.repository.no_remote")
    }

    @FXML
    private fun onCellPressed(event: MouseEvent) {
        if (event.button == MouseButton.PRIMARY) {
            openRepository()
        }
    }

    @FXML
    private fun openRepository() {
        menuView.openRepository(repository)
    }

    @FXML
    private fun openInFileBrowser() {
        SystemManager.get().openInFileBrowser(repository.directory).begin()
    }

    private fun openInBrowser() {
        remote?.let { Application.getInstance().hostServices.showDocument(it.url.toURI().toString()) }
    }

}