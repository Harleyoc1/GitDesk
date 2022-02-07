package com.harleyoconnor.gitdesk.ui.util

import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog

fun createErrorDialogue(errorTitle: String, cause: Throwable): Alert {
    val alert = Alert(Alert.AlertType.ERROR)
    alert.headerText = errorTitle
    alert.contentText = TRANSLATIONS_BUNDLE.getString("dialogue.error.content", cause.toString())
    return alert
}

fun createConfirmationDialogue(actionId: String): Alert {
    val alert = Alert(Alert.AlertType.CONFIRMATION)
    alert.headerText = TRANSLATIONS_BUNDLE.getString("dialogue.confirm.$actionId.header")
    alert.headerText = TRANSLATIONS_BUNDLE.getString("dialogue.confirm.$actionId.content")
    return alert
}