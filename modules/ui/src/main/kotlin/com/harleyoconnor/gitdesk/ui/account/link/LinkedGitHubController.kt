package com.harleyoconnor.gitdesk.ui.account.link

import com.harleyoconnor.gitdesk.data.account.GitHubAccount
import com.harleyoconnor.gitdesk.data.account.Session
import com.harleyoconnor.gitdesk.data.account.unlinkGitHubRequest
import com.harleyoconnor.gitdesk.data.remote.User
import com.harleyoconnor.gitdesk.data.remote.github.GitHubNetworking
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.style.BOTTOM_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.style.TOP_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Circle

/**
 * @author Harley O'Connor
 */
class LinkedGitHubController : ViewController<LinkedGitHubController.Context> {

    object Loader: ResourceViewLoader<Context, LinkedGitHubController, VBox>(
        UIResource("/ui/layouts/account/tabs/link/LinkedGitHub.fxml")
    )

    class Context(val parent: GitHubLinkTab, val account: GitHubAccount): ViewController.Context

    private lateinit var parent: GitHubLinkTab
    private var user: User? = null

    @FXML
    private lateinit var userCellContextMenu: ContextMenu

    @FXML
    private lateinit var userCell: HBox

    @FXML
    private lateinit var avatar: Circle
    @FXML
    private lateinit var usernameLabel: Label

    @FXML
    private fun initialize() {
        userCell.pseudoClassStateChanged(TOP_PSEUDO_CLASS, true)
        userCell.pseudoClassStateChanged(BOTTOM_PSEUDO_CLASS, true)

        userCell.setOnContextMenuRequested {
            userCellContextMenu.show(userCell, it.screenX, it.screenY)
        }
    }

    override fun setup(context: Context) {
        this.parent = context.parent
        this.user = GitHubNetworking.getUser(context.account.username)
        user?.let {
            avatar.fill = ImagePattern(Image(it.avatarUrl.toExternalForm()))
        }
        usernameLabel.text = context.account.username
    }

    @FXML
    private fun unlink(event: ActionEvent) {
        unlinkGitHubRequest(Session.getOrLoad()!!).join()
            .apply {
                parent.toUnlinkedView()
            }.logIfError("Unlinking GitHub account.")
    }

    @FXML
    private fun openProfileInBrowser(event: ActionEvent) {
        user?.let { Application.getInstance().hostServices.showDocument(it.url.toExternalForm()) }
    }

}