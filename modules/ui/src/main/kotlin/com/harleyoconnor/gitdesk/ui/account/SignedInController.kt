package com.harleyoconnor.gitdesk.ui.account

import com.harleyoconnor.gitdesk.data.account.Account
import com.harleyoconnor.gitdesk.data.account.Session
import com.harleyoconnor.gitdesk.ui.util.load
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.layout.BorderPane

/**
 *
 * @author Harley O'Connor
 */
class SignedInController {

    companion object {
        fun load(parent: AccountWindow, account: Account): BorderPane {
            val fxml = load<BorderPane, SignedInController>("account/SignedInRoot")
            fxml.controller.setup(parent, account)
            return fxml.root
        }
    }

    private lateinit var parent: AccountWindow

    @FXML
    private lateinit var root: BorderPane

    @FXML
    private lateinit var detailsTabButton: RadioButton

    @FXML
    private lateinit var usernameLabel: Label

    private lateinit var account: Account

    private fun setup(parent: AccountWindow, account: Account) {
        this.parent = parent
        this.account = account
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