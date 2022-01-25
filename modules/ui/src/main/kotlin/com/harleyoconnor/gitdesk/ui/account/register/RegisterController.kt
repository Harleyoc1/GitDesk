package com.harleyoconnor.gitdesk.ui.account.register

import com.harleyoconnor.gitdesk.data.account.AccountCreationData
import com.harleyoconnor.gitdesk.data.account.RegistrationData
import com.harleyoconnor.gitdesk.data.account.VerificationData
import com.harleyoconnor.gitdesk.data.account.registerRequest
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.form.validation.FieldValidator
import com.harleyoconnor.gitdesk.ui.form.validation.MatchesFieldValidator
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

/**
 *
 * @author Harley O'Connor
 */
class RegisterController : ViewController<RegisterController.Context> {

    object Loader: ResourceViewLoader<Context, RegisterController, VBox>(
        UIResource("/ui/layouts/account/tabs/register/Root.fxml")
    )

    class Context(val parent: RegisterTab): ViewController.Context

    private lateinit var parent: RegisterTab

    @FXML
    private lateinit var usernameField: TextField

    @FXML
    private lateinit var emailField: TextField

    @FXML
    private lateinit var passwordField: PasswordField

    @FXML
    private lateinit var confirmPasswordField: PasswordField

    @FXML
    private lateinit var registerButton: Button

    @FXML
    private fun initialize() {
        confirmPasswordField.setOrAppendValidator(
            MatchesFieldValidator({ passwordField.getTextUnvalidated() }, "validation.password_mismatch")
        )
    }

    override fun setup(context: Context) {
        parent = context.parent
    }

    fun clearAllFields() {
        usernameField.clearText()
        emailField.clearText()
        passwordField.clearText()
        confirmPasswordField.clearText()
    }

    @FXML
    private fun register(event: ActionEvent) {
        try {
            register(
                AccountCreationData(
                    usernameField.getText(),
                    emailField.getText(),
                    confirmPasswordField.getText()
                )
            )
        } catch (ignored: FieldValidator.InvalidException) {
        }
    }

    private fun register(creationData: AccountCreationData) {
        registerRequest(creationData).thenAccept {
            it.apply { registrationData ->
                Platform.runLater {
                    toEmailVerification(creationData, registrationData)
                }
            }
        }.join()
    }

    private fun toEmailVerification(
        creationData: AccountCreationData,
        registrationData: RegistrationData
    ) {
        parent.toEmailVerification(VerificationData(creationData.email, registrationData.clientCode))
    }

    @FXML
    private fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            registerButton.fire()
        }
    }
}