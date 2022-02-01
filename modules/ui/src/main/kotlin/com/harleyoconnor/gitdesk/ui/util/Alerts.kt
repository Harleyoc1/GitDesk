package com.harleyoconnor.gitdesk.ui.util

import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
import javafx.scene.control.Alert

fun showErrorDialogue(errorTitle: String, cause: Throwable) {
    val alert = Alert(Alert.AlertType.ERROR)
    alert.headerText = errorTitle
    alert.contentText = TRANSLATIONS_BUNDLE.getString("dialogue.error.content", cause.toString())
    alert.show()
}