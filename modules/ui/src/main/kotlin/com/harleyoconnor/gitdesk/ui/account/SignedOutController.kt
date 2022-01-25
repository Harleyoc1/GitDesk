package com.harleyoconnor.gitdesk.ui.account

import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.account.register.RegisterTab
import com.harleyoconnor.gitdesk.ui.account.signin.SignInController
import com.harleyoconnor.gitdesk.ui.util.Tab
import com.harleyoconnor.gitdesk.ui.util.setOnSelected
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.fxml.FXML
import javafx.scene.control.RadioButton
import javafx.scene.layout.BorderPane

/**
 *
 * @author Harley O'Connor
 */
class SignedOutController : ViewController<SignedOutController.Context> {

    object Loader: ResourceViewLoader<Context, SignedOutController, BorderPane>(
        UIResource("/ui/layouts/account/SignedOutRoot.fxml")
    )

    class Context(val parent: AccountWindow): ViewController.Context

    private lateinit var parent: AccountWindow

    @FXML
    private lateinit var root: BorderPane

    @FXML
    private lateinit var registerTabButton: RadioButton

    @FXML
    private lateinit var signInTabButton: RadioButton

    private val registerTab: Tab by lazy {
        RegisterTab(this.parent::toSignedInView) {
            root.center = it
        }
    }

    private val signInTab: Tab by lazy {
        Tab(SignInController.Loader.load(SignInController.Context(this.parent::toSignedInView)).root) {
            root.center = it
        }
    }

    override fun setup(context: Context) {
        this.parent = context.parent
        registerTabButton.setOnSelected {
            registerTab.open()
        }
        signInTabButton.setOnSelected {
            signInTab.open()
        }
        registerTabButton.fire()
    }
}