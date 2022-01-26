package com.harleyoconnor.gitdesk.ui.repository.branch

import com.harleyoconnor.gitdesk.data.remote.Platform
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.RemoteRepositoryReference
import com.harleyoconnor.gitdesk.data.remote.github.GitHubRemoteRepository
import com.harleyoconnor.gitdesk.git.repository.Branch
import com.harleyoconnor.gitdesk.git.repository.RemoteBranch
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.style.CHECKED_OUT_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.util.getIcon
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.toGitDisplayUrl
import com.harleyoconnor.gitdesk.util.xml.SVGCache
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import java.io.File

/**
 *
 * @author Harley O'Connor
 */
class BranchCellController : ViewController<BranchCellController.Context> {

    object Loader: ResourceViewLoader<Context, BranchCellController, HBox>(
        UIResource("/ui/layouts/repository/branches/BranchCell.fxml")
    )

    class Context(val parent: BranchesController, val branch: Branch): ViewController.Context

    private lateinit var parent: BranchesController

    private lateinit var branch: Branch

    @FXML
    private lateinit var root: HBox

    @FXML
    private lateinit var contextMenu: ContextMenu

    @FXML
    private lateinit var openInBrowserItem: MenuItem

    @FXML
    private lateinit var editItem: MenuItem

    @FXML
    private lateinit var deleteItem: MenuItem

    @FXML
    private lateinit var label: Label

    @FXML
    private lateinit var checkedOutIcon: SVGIcon

    @FXML
    private lateinit var remoteLabel: Label

    @FXML
    private lateinit var remotePlatformIcon: SVGIcon

    @FXML
    fun initialize() {
        root.setOnContextMenuRequested {
            deleteItem.isDisable = branch.isCheckedOut()
            contextMenu.show(root, it.screenX, it.screenY)
        }
    }

    override fun setup(context: Context) {
        this.parent = context.parent
        this.branch = context.branch
        label.text = branch.name
        checkedOutIcon.pseudoClassStateChanged(CHECKED_OUT_PSEUDO_CLASS, branch.isCheckedOut())
        branch.getUpstream()?.let { updateUiWithUpstream(it) }
        if (branch.isRemoteBranch()) {
            updateUiWithUpstream(branch.asRemoteBranch())
            contextMenu.items.remove(editItem)
            contextMenu.items.remove(deleteItem)
        }
    }

    private fun updateUiWithUpstream(upstream: RemoteBranch) {
        updateUpstreamLabelAndIcon(upstream)
        openInBrowserItem.isDisable = false
        openInBrowserItem.setOnAction { openInBrowser(upstream) }
    }

    private fun updateUpstreamLabelAndIcon(upstream: RemoteBranch) {
        val remote = upstream.remote.remote
        if (remote is RemoteRepositoryReference) {
            remoteLabel.text = remote.name.getFullName()
            if (remote.platform == Platform.GITHUB) {
                remotePlatformIcon.setupFromSvg(remote.platform.getIcon())
            }
        } else {
            remoteLabel.text = remote.url.toGitDisplayUrl()
            remotePlatformIcon.setupFromSvg(SVGCache.getOrLoad(UIResource("/ui/icons/web.svg")))
        }
        remoteLabel.text += "/" + upstream.name
    }

    @FXML
    private fun onCellPressed(event: MouseEvent) {
        if (event.button == MouseButton.PRIMARY) {
            parent.checkOutBranch(branch)
            parent.closeWindow()
        }
    }

    @FXML
    private fun checkout(event: ActionEvent) {
        parent.checkOutBranch(branch)
        parent.closeWindow()
    }

    private fun openInBrowser(upstream: RemoteBranch) {
        Application.getInstance().hostServices.showDocument(getBranchLink(upstream))
    }

    private fun getBranchLink(upstream: RemoteBranch): String {
        return upstream.remote.remote.url.toURI().toString() +
                File.separatorChar + "tree" + File.separatorChar + upstream.name
    }

    @FXML
    private fun edit(event: ActionEvent) {
        parent.openEditBranchView(branch)
    }

    @FXML
    private fun delete(event: ActionEvent) {
        if (!branch.isCheckedOut()) {
            branch.delete().begin()
            parent.removeBranchCell(branch)
        }
    }

}
