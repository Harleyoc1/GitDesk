package com.harleyoconnor.gitdesk.ui.util

import javafx.scene.Node

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