package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.util.xml.SVGCache
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import java.io.FileNotFoundException

/**
 * @author Harley O'Connor
 */
abstract class RemoteCellController {

    @FXML
    protected lateinit var root: HBox

    @FXML
    private lateinit var contextMenu: ContextMenu

    @FXML
    private lateinit var label: Label

    @FXML
    private lateinit var languageIcon: SVGIcon

    protected lateinit var remote: Remote

    @FXML
    open fun initialize() {
        root.setOnContextMenuRequested {
            contextMenu.show(root, it.screenX, it.screenY)
        }
    }

    protected open fun initializeWithRemote(remote: Remote) {
        this.remote = remote
        if (remote is RemoteRepository) {
            label.text = remote.name.getFullName()
            tryGetLanguageIconPath(remote)?.let {
                languageIcon.setPath(it)
            }
        } else {
            label.text = remote.url.toExternalForm()
        }
    }

    private fun tryGetLanguageIconPath(remote: RemoteRepository): String? {
        return try {
            val path = "/ui/icons/languages/${remote.language?.lowercase()}.svg"
            SVGCache.getOrLoad(UIResource(path))
            path
        } catch (e: FileNotFoundException) {
            null
        }
    }

    fun openInBrowser(event: ActionEvent) {
        Application.getInstance().hostServices.showDocument(remote.url.toURI().toString())
    }

}