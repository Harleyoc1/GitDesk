package com.harleyoconnor.gitdesk.ui.repository.branch

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.github.GitHubRemoteRepository
import com.harleyoconnor.gitdesk.git.repository.Branch
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.style.CHECKED_OUT_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.util.getIcon
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.util.toGitDisplayUrl
import com.harleyoconnor.gitdesk.util.xml.SVGCache
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import java.io.File

/**
 *
 * @author Harley O'Connor
 */
class BranchCellController {

    companion object {
        fun load(parent: BranchesController, branch: Branch): HBox {
            val fxml = load<HBox, BranchCellController>("repository/branches/BranchCell")
            fxml.controller.setup(parent, branch)
            return fxml.root
        }
    }

    private lateinit var parent: BranchesController

    private lateinit var branch: Branch

    @FXML
    private lateinit var root: HBox

    @FXML
    private lateinit var contextMenu: ContextMenu

    @FXML
    private lateinit var openInBrowserItem: MenuItem

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
            contextMenu.show(root, it.screenX, it.screenY)
        }
    }

    private fun setup(parent: BranchesController, branch: Branch) {
        this.parent = parent
        this.branch = branch
        label.text = branch.name
        checkedOutIcon.pseudoClassStateChanged(CHECKED_OUT_PSEUDO_CLASS, branch.checkedOut)
        branch.upstream?.let { updateUiWithUpstream(it) }
    }

    private fun updateUiWithUpstream(upstream: Branch.Upstream) {
        updateUpstreamLabelAndIcon(upstream)
        openInBrowserItem.isDisable = false
        openInBrowserItem.setOnAction { openInBrowser(upstream) }
    }

    private fun updateUpstreamLabelAndIcon(upstream: Branch.Upstream) {
        val remote = upstream.remote.remote
        if (remote is RemoteRepository) {
            remoteLabel.text = remote.name.getFullName()
            if (remote is GitHubRemoteRepository) {
                remotePlatformIcon.setupFromSvg(remote.getIcon())
            }
        } else {
            remoteLabel.text = remote.url.toGitDisplayUrl()
            remotePlatformIcon.setupFromSvg(SVGCache.getOrLoad(UIResource("/ui/icons/web.svg")))
        }
        if (upstream.branch != null) {
            remoteLabel.text += "/" + upstream.branch
        }
    }

    @FXML
    private fun onCellPressed(event: MouseEvent) {

    }

    @FXML
    private fun checkout(event: ActionEvent) {
        branch.checkOut().begin()
    }

    private fun openInBrowser(upstream: Branch.Upstream) {
        Application.getInstance().hostServices.showDocument(getBranchLink(upstream))
    }

    private fun getBranchLink(upstream: Branch.Upstream): String {
        return upstream.remote.remote.url.toURI().toString() +
                if (upstream.branch != null)
                    File.separatorChar + "tree" + File.separatorChar + upstream.branch
                else ""
    }

}
