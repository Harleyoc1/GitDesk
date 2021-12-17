package com.harleyoconnor.gitdesk.ui.node

import javafx.scene.control.TextField

/**
 *
 * @author Harley O'Connor
 */
class PasswordField : ValidatedField<TextField>() {

    init {
        load("/ui/nodes/PasswordField.fxml", this)
    }

}