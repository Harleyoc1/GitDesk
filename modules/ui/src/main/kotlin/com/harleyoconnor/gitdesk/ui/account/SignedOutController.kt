package com.harleyoconnor.gitdesk.ui.account

import com.harleyoconnor.gitdesk.ui.account.register.RegisterTab
import com.harleyoconnor.gitdesk.ui.account.signin.SignInController
import com.harleyoconnor.gitdesk.ui.util.Tab
import com.harleyoconnor.gitdesk.ui.util.load
import com.harleyoconnor.gitdesk.ui.util.setOnSelected
import javafx.fxml.FXML
import javafx.scene.control.RadioButton
import javafx.scene.layout.BorderPane

/**
 *
 * @author Harley O'Connor
 */
class SignedOutController {

    companion object {
        fun load(parent: AccountWindow): BorderPane {
            val fxml = load<BorderPane, SignedOutController>("account/SignedOutRoot")
            fxml.controller.setup(parent)
            return fxml.root
        }
    }

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
        Tab(SignInController.load(this.parent::toSignedInView)) {
            root.center = it
        }
    }

    fun setup(parent: AccountWindow) {
        this.parent = parent
        registerTabButton.setOnSelected {
            openRegisterTab()
        }
        signInTabButton.setOnSelected {
            openSignInTab()
        }
        registerTabButton.fire()
    }

    private fun openRegisterTab() {
        root.center = registerTab.node
    }

    private fun openSignInTab() {
        root.center = signInTab.node
    }
}