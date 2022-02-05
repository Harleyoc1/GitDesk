package com.harleyoconnor.gitdesk.ui.account.link

import com.harleyoconnor.gitdesk.data.account.Account
import com.harleyoconnor.gitdesk.data.account.GitHubLinkingData
import com.harleyoconnor.gitdesk.data.account.Session
import com.harleyoconnor.gitdesk.data.account.getGitHubAccountRequest
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class FinaliseGitHubLinkController : ViewController<FinaliseGitHubLinkController.Context> {

    object Loader : ResourceViewLoader<Context, FinaliseGitHubLinkController, VBox>(
        UIResource("/ui/layouts/account/tabs/link/FinaliseGitHubLink.fxml")
    )

    class Context(val parent: GitHubLinkTab, val account: Account, val linkingData: GitHubLinkingData) :
        ViewController.Context

    private lateinit var parent: GitHubLinkTab
    private lateinit var account: Account
    private lateinit var linkingData: GitHubLinkingData

    @FXML
    private lateinit var doneButton: Button

    override fun setup(context: Context) {
        parent = context.parent
        account = context.account
        linkingData = context.linkingData
    }

    @FXML
    private fun cancel(event: ActionEvent) {
        parent.toUnlinkedView()
    }

    @FXML
    private fun done(event: ActionEvent) {
        getGitHubAccountRequest(Session.getOrLoad()!!).thenAccept {
            it.apply { account ->
                Platform.runLater {
                    parent.toLinkedView(account)
                }
            }.logIfError("Getting GitHub account data failed.")
        }.join()
    }

    @FXML
    private fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            doneButton.fire()
        }
    }
}