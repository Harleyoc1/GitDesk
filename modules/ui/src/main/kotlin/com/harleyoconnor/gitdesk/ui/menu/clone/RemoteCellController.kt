package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.RemoteRepositoryReference
import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.view.ViewController
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
abstract class RemoteCellController<C : RemoteCellController.Context> : ViewController<C> {

    open class Context(val remote: Remote): ViewController.Context

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
    fun initialize() {
        root.setOnContextMenuRequested {
            contextMenu.show(root, it.screenX, it.screenY)
        }
    }

    override fun setup(context: C) {
        this.remote = if (context.remote is RemoteRepositoryReference)
            context.remote.getRemoteRepository() ?: context.remote
        else context.remote
        this.setContentFor(remote)
    }

    private fun setContentFor(remote: Remote) {
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