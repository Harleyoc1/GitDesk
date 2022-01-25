package com.harleyoconnor.gitdesk.ui.util

import com.harleyoconnor.gitdesk.ui.style.BOTTOM_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.style.TOP_PSEUDO_CLASS
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.RadioButton

fun Node.enableTopClass() {
    this.pseudoClassStateChanged(TOP_PSEUDO_CLASS, true)
}

fun Node.disableTopClass() {
    this.pseudoClassStateChanged(TOP_PSEUDO_CLASS, false)
}

fun Node.enableBottomClass() {
    this.pseudoClassStateChanged(BOTTOM_PSEUDO_CLASS, true)
}

fun Node.disableBottomClass() {
    this.pseudoClassStateChanged(BOTTOM_PSEUDO_CLASS, false)
}

fun RadioButton.setOnSelected(action: () -> Unit) {
    this.selectedProperty().addListener { _, _, newValue ->
        if (newValue) {
            action()
        }
    }
}

fun CheckBox.setOnActions(selected: () -> Unit, deselected: () -> Unit) {
    this.selectedProperty().addListener { _, _, newValue ->
        if (newValue) {
            selected()
        } else {
            deselected()
        }
    }
}