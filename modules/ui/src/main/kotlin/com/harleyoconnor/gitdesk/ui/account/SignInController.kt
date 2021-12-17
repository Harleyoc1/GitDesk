package com.harleyoconnor.gitdesk.ui.account

import com.harleyoconnor.gitdesk.ui.node.PasswordField
import com.harleyoconnor.gitdesk.ui.node.TextField
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

/**
 *
 * @author Harley O'Connor
 */
class SignInController {

    @FXML
    private lateinit var usernameField: TextField
    @FXML
    private lateinit var passwordField: PasswordField

    @FXML
    private lateinit var signInButton: Button

    @FXML
    private fun clear() {
        usernameField.clearText()
        passwordField.clearText()
    }

    @FXML
    private fun signIn(event: ActionEvent) {

    }

    @FXML
    private fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            signInButton.fire()
        }
    }

}