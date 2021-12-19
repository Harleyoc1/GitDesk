package com.harleyoconnor.gitdesk.ui.util

import javafx.scene.Node
import javafx.scene.control.RadioButton

fun Node.addTopClass() {
    this.styleClass.add("top")
}

fun Node.removeTopClass() {
    this.styleClass.remove("top")
}

fun Node.addBottomClass() {
    this.styleClass.add("bottom")
}

fun Node.removeBottomClass() {
    this.styleClass.remove("bottom")
}

fun RadioButton.setOnSelected(action: () -> Unit) {
    this.selectedProperty().addListener { _, _, newValue ->
        if (newValue) {
            action()
        }
    }
}