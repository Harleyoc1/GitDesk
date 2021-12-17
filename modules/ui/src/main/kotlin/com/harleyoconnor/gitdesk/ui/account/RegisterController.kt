package com.harleyoconnor.gitdesk.ui.account

import com.harleyoconnor.gitdesk.ui.node.PasswordField
import com.harleyoconnor.gitdesk.ui.node.TextField
import com.harleyoconnor.gitdesk.ui.validation.FieldValidator
import com.harleyoconnor.gitdesk.ui.validation.MatchesFieldValidator
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

/**
 *
 * @author Harley O'Connor
 */
class RegisterController {

    @FXML
    private lateinit var usernameField: TextField

    @FXML
    private lateinit var emailField: TextField

    @FXML
    private lateinit var passwordField: PasswordField

    @FXML
    private lateinit var confirmPasswordField: PasswordField

    @FXML
    private lateinit var createButton: Button

    @FXML
    private fun initialize() {
        confirmPasswordField.setOrAppendValidator(
            MatchesFieldValidator({ passwordField.getTextUnvalidated() }, "validation.password_mismatch")
        )
    }

    @FXML
    private fun clear(event: ActionEvent) {
        usernameField.clearText()
        emailField.clearText()
        passwordField.clearText()
        confirmPasswordField.clearText()
    }

    @FXML
    private fun register(event: ActionEvent) {
        try {
            val username = usernameField.getText()
            val email = emailField.getText()
            val password = confirmPasswordField.getText()
        } catch (ignored: FieldValidator.InvalidException) { }
    }

    @FXML
    private fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            createButton.fire()
        }
    }
}