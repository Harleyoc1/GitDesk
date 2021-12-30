package com.harleyoconnor.gitdesk.ui.account.register

import com.harleyoconnor.gitdesk.data.account.AccountCreationData
import com.harleyoconnor.gitdesk.data.account.RegistrationData
import com.harleyoconnor.gitdesk.data.account.VerificationData
import com.harleyoconnor.gitdesk.data.account.registerRequest
import com.harleyoconnor.gitdesk.ui.node.PasswordField
import com.harleyoconnor.gitdesk.ui.node.TextField
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.ui.validation.FieldValidator
import com.harleyoconnor.gitdesk.ui.validation.MatchesFieldValidator
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
class RegisterController {

    companion object {
        fun load(parent: RegisterTab): com.harleyoconnor.gitdesk.ui.util.LoadedFXML<VBox, RegisterController> {
            val fxml = load<VBox, RegisterController>("account/tabs/register/Root")
            fxml.controller.parent = parent
            return fxml
        }
    }

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