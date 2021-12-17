package com.harleyoconnor.gitdesk.ui.node

import javafx.scene.control.TextField

/**
 *
 * @author Harley O'Connor
 */
class TextField : ValidatedField<TextField>() {

    init {
        load("/ui/nodes/TextField.fxml", this)
    }

}