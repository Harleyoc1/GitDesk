package com.harleyoconnor.gitdesk.ui.account

import com.harleyoconnor.gitdesk.ui.util.Tab
import com.harleyoconnor.gitdesk.ui.util.loadLayout
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
    private lateinit var openRegisterButton: RadioButton

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
        openRegisterButton.fire()
    }

    @FXML
    private fun openRegisterTab() {
        root.center = registerTab.node
    }

    @FXML
    private fun openSignInTab() {
        root.center = signInTab.node
    }
}