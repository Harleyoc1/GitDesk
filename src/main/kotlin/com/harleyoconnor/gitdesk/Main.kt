package com.harleyoconnor.gitdesk

import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.util.logging.setLoggingDirectoryProperty
import javafx.application.Application.launch
import org.apache.logging.log4j.LogManager

fun main(vararg args: String) {
    setSystemProperties()

    try {
        launch(Application::class.java, *args)
    } catch (t: Throwable) {
        LogManager.getLogger().error("Application has crashed due to error or exception.", t)
        throw t
    }
}

private fun setSystemProperties() {
    setLoggingDirectoryProperty()
    System.setProperty("prism.lcdtext", "false") // Fixes subtle text tint on MacOS.
}