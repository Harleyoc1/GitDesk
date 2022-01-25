package com.harleyoconnor.gitdesk.ui.account.signin

import com.harleyoconnor.gitdesk.data.account.Account
import com.harleyoconnor.gitdesk.data.account.AccountCredentials
import com.harleyoconnor.gitdesk.data.account.signInRequest
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.form.validation.FieldValidator
import com.harleyoconnor.gitdesk.ui.node.PasswordField
import com.harleyoconnor.gitdesk.ui.node.TextField
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import java.util.function.Consumer

/**
 *
 * @author Harley O'Connor
 */
class SignInController : ViewController<SignInController.Context> {

    object Loader: ResourceViewLoader<Context, SignInController, VBox>(
        UIResource("/ui/layouts/account/tabs/sign_in/Root.fxml")
    )

    class Context(val openSignedInViewCallback: Consumer<Account>): ViewController.Context

    private lateinit var openSignedInViewCallback: Consumer<Account>

    @FXML
    private lateinit var usernameField: TextField
    @FXML
    private lateinit var passwordField: PasswordField

    @FXML
    private lateinit var signInButton: Button

    override fun setup(context: Context) {
        openSignedInViewCallback = context.openSignedInViewCallback
    }

    @FXML
    private fun clear() {
        usernameField.clearText()
        passwordField.clearText()
    }

    @FXML
    private fun signIn(event: ActionEvent) {
        try {
            val credentials = AccountCredentials(usernameField.getText(), passwordField.getText())
            signInRequest(credentials).thenAccept {
                it.apply { session ->
                    session.save()
                    Platform.runLater {
                        session.getAccount()?.let { account ->
                            openSignedInViewCallback.accept(account)
                        }
                    }
                }
            }.join()
        } catch (ignored: FieldValidator.InvalidException) {}
    }

    @FXML
    private fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            signInButton.fire()
        }
    }

}