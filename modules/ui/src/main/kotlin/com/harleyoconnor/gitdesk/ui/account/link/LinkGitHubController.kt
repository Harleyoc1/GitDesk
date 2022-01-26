package com.harleyoconnor.gitdesk.ui.account.link

import com.harleyoconnor.gitdesk.data.account.Account
import com.harleyoconnor.gitdesk.data.account.GitHubLinkingData
import com.harleyoconnor.gitdesk.data.account.Session
import com.harleyoconnor.gitdesk.data.account.linkGitHubRequest
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.TextField
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.network.URIBuilder
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import javafx.scene.shape.Circle
import java.net.URI

/**
 * @author Harley O'Connor
 */
class LinkGitHubController : ViewController<LinkGitHubController.Context> {

    companion object {
        private const val GITHUB_CLIENT_ID = "a2c98a0415856920b90a"
    }

    object Loader: ResourceViewLoader<Context, LinkGitHubController, VBox>(
        UIResource("/ui/layouts/account/tabs/link/LinkGitHub.fxml")
    )

    class Context(val parent: GitHubLinkTab, val account: Account): ViewController.Context

    private lateinit var parent: GitHubLinkTab
    private lateinit var account: Account

    @FXML
    private lateinit var usernameField: TextField

    @FXML
    private lateinit var errorLabel: Label

    @FXML
    private lateinit var linkButton: Button

    @FXML
    private fun initialize() {
        usernameField.onTextChanged { _, _ ->
            errorLabel.text = ""
        }
    }

    override fun setup(context: Context) {
        parent = context.parent
        account = context.account
    }

    @FXML
    private fun clearAllFields(event: ActionEvent) {
        usernameField.clearText()
    }

    @FXML
    private fun link(event: ActionEvent) {
        val username = usernameField.getTextOrNull() ?: return
        linkGitHubRequest(username, Session.load()!!)
            .thenAccept { response ->
                response.apply {
                    Platform.runLater {
                        parent.toFinaliseLinkView(it)
                        openRegisterLink(it)
                    }
                }.ifError {
                    populateErrorField(it)
                }.logIfError("Link GitHub request failed.")
            }.join()
    }

    private fun populateErrorField(it: Int) {
        if (it >= 500) {
            errorLabel.text = TRANSLATIONS_BUNDLE.getString("error.unknown.server")
        } else if (it in 400 until 500) {
            errorLabel.text = TRANSLATIONS_BUNDLE.getString("error.unknown.invalid")
        }
    }

    private fun openRegisterLink(linkingData: GitHubLinkingData) {
        Application.getInstance().hostServices.showDocument(
            createRegisterLink(linkingData).toString()
        )
    }

    private fun createRegisterLink(linkingData: GitHubLinkingData): URI {
        return URIBuilder()
            .https()
            .append("github.com/login/oauth/authorize")
            .parameter("client_id", GITHUB_CLIENT_ID)
            .parameter("login", linkingData.username)
            .parameter("scope", "repo workflow admin:org user")
            .parameter("state", linkingData.state)
            .parameter(
                "redirect_uri", URIBuilder()
                    .https()
                    .append("harleyoconnor.com/gitdesk/link/github/")
                    .toString()
            )
            .build()
    }

    @FXML
    private fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            linkButton.fire()
        }
    }
}