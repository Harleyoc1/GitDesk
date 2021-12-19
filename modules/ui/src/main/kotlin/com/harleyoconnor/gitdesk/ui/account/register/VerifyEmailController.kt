package com.harleyoconnor.gitdesk.ui.account.register

import com.harleyoconnor.gitdesk.data.account.VerificationData
import com.harleyoconnor.gitdesk.data.account.verifyEmailRequest
import com.harleyoconnor.gitdesk.ui.node.TextField
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.ui.validation.FieldValidator
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class VerifyEmailController {

    companion object {
        fun load(parent: RegisterTab, verificationData: VerificationData): VBox {
            val fxml = load<VBox, VerifyEmailController>("account/tabs/register/VerifyEmail")
            fxml.controller.parent = parent
            fxml.controller.verificationData = verificationData
            return fxml.root
        }
    }

    private lateinit var parent: RegisterTab

    private lateinit var verificationData: VerificationData

    @FXML
    private lateinit var finishButton: Button

    @FXML
    private lateinit var verificationCodeField: TextField

    @FXML
    private fun cancel(event: ActionEvent) {
        parent.toRootAndClear()
    }

    @FXML
    private fun back(event: ActionEvent) {
        parent.toRoot()
    }

    @FXML
    private fun finish(event: ActionEvent) {
        try {
            verificationData.verificationCode = verificationCodeField.getText()
            verifyEmailRequest(verificationData).thenAccept {
                it.apply { session ->
                    session.save()
                }
            }.join()
        } catch (ignored: FieldValidator.InvalidException) { }
    }

    @FXML
    private fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            finishButton.fire()
        }
    }
}