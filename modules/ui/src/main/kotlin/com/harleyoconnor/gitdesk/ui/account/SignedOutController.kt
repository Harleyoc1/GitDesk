package com.harleyoconnor.gitdesk.ui.account

import com.harleyoconnor.gitdesk.ui.util.Tab
import com.harleyoconnor.gitdesk.ui.util.loadLayout
import com.harleyoconnor.gitdesk.ui.util.setOnSelected
import javafx.fxml.FXML
import javafx.scene.control.RadioButton
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox

/**
 *
 * @author Harley O'Connor
 */
class SignedOutController {

    @FXML
    private lateinit var root: BorderPane

    @FXML
    private lateinit var registerTabButton: RadioButton

    @FXML
    private lateinit var signInTabButton: RadioButton

    private val registerTab: Tab by lazy {
        Tab(loadLayout<VBox>("account/tabs/register/Root")) {
            root.center = it
        }
    }

    private val signInTab: Tab by lazy {
        Tab(loadLayout<VBox>("account/tabs/sign_in/Root")) {
            root.center = it
        }
    }

    @FXML
    private fun initialize() {
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