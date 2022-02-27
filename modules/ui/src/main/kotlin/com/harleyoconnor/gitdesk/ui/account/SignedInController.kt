package com.harleyoconnor.gitdesk.ui.account

import com.harleyoconnor.gitdesk.data.account.Account
import com.harleyoconnor.gitdesk.data.account.Session
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.account.link.GitHubLinkTab
import com.harleyoconnor.gitdesk.ui.util.Tab
import com.harleyoconnor.gitdesk.ui.util.setOnSelected
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.layout.BorderPane

/**
 *
 * @author Harley O'Connor
 */
class SignedInController : ViewController<SignedInController.Context> {

    object Loader: ResourceViewLoader<Context, SignedInController, BorderPane>(
        UIResource("/ui/layouts/account/SignedInRoot.fxml")
    )

    class Context(val parent: AccountWindow, val account: Account): ViewController.Context

    private lateinit var parent: AccountWindow
    private lateinit var account: Account

    @FXML
    private lateinit var root: BorderPane

//    @FXML
//    private lateinit var detailsTabButton: RadioButton

    @FXML
    private lateinit var gitHubLinkTabButton: RadioButton

    private val gitHubLinkTab: Tab by lazy {
        GitHubLinkTab(account) {
            root.center = it
        }
    }

    @FXML
    private lateinit var usernameLabel: Label

    override fun setup(context: Context) {
        this.parent = context.parent
        this.account = context.account
        usernameLabel.text = account.username
        gitHubLinkTabButton.setOnSelected {
            gitHubLinkTab.open()
        }
        gitHubLinkTabButton.fire()
    }

    @FXML
    private fun signOut(event: ActionEvent) {
        Session.getOrLoad()?.delete()?.let {
            parent.toSignedOutView()
        }
    }
}