package com.harleyoconnor.gitdesk.ui.node

import javafx.scene.control.TextArea

/**
 *
 * @author Harley O'Connor
 */
class TextArea : ValidatedField<TextArea>() {

    init {
        load("/ui/nodes/TextArea.fxml", this)
    }

}