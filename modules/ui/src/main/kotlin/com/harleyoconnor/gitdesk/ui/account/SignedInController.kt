package com.harleyoconnor.gitdesk.ui.account

import com.harleyoconnor.gitdesk.data.account.Account
import com.harleyoconnor.gitdesk.data.account.Session
import com.harleyoconnor.gitdesk.ui.UIResource
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

    @FXML
    private lateinit var detailsTabButton: RadioButton

    @FXML
    private lateinit var usernameLabel: Label

    override fun setup(context: Context) {
        this.parent = context.parent
        this.account = context.account
        usernameLabel.text = account.username
        detailsTabButton.fire()
    }

    @FXML
    private fun openDetailsTab(event: ActionEvent) {

    }

    @FXML
    private fun openGitHubTab(event: ActionEvent) {

    }

    @FXML
    private fun signOut(event: ActionEvent) {
        Session.delete()
        parent.toSignedOutView()
    }
}