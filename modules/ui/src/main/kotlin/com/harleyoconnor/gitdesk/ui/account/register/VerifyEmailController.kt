package com.harleyoconnor.gitdesk.ui.account.register

import com.harleyoconnor.gitdesk.data.account.VerificationData
import com.harleyoconnor.gitdesk.data.account.verifyEmailRequest
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.form.validation.FieldValidator
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
 * @author Harley O'Connor
 */
class VerifyEmailController : ViewController<VerifyEmailController.Context> {

    object Loader: ResourceViewLoader<Context, VerifyEmailController, VBox>(
        UIResource("/ui/layouts/account/tabs/register/VerifyEmail.fxml")
    )

    class Context(val parent: RegisterTab, val verificationData: VerificationData): ViewController.Context

    private lateinit var parent: RegisterTab

    private lateinit var verificationData: VerificationData

    @FXML
    private lateinit var finishButton: Button

    @FXML
    private lateinit var verificationCodeField: TextField

    override fun setup(context: Context) {
        parent = context.parent
        verificationData = context.verificationData
    }

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
                    Platform.runLater {
                        session.getAccount()?.let { account ->
                            parent.toSignedInView(account)
                        }
                    }
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