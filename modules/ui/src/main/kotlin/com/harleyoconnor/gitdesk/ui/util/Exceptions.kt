package com.harleyoconnor.gitdesk.ui.util

import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import javafx.scene.control.Alert
import org.apache.logging.log4j.LogManager

fun logAndCreateDialogue(errorTitleKey: String, cause: Throwable): Alert {
    val errorTitle = TRANSLATIONS_BUNDLE.getString(errorTitleKey)
    LogManager.getLogger().error(errorTitle, cause)
    return createErrorDialogue(errorTitle, cause)
}