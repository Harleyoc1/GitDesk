package com.harleyoconnor.gitdesk

import com.harleyoconnor.gitdesk.data.remote.github.GitHubNetworking
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.highlighting.KeywordBasedSyntaxHighlighter
import com.harleyoconnor.gitdesk.ui.highlighting.setupHighlightingDirectory
import com.harleyoconnor.gitdesk.util.logging.setLoggingDirectoryProperty
import javafx.application.Application.launch
import org.apache.logging.log4j.LogManager

fun main(vararg args: String) {
    Runtime.getRuntime().addShutdownHook(Thread {
        Application.getInstance().stop()
    })
    setSystemProperties()
    setupHighlightingDirectory()
    GitHubNetworking.registerTypes()
    KeywordBasedSyntaxHighlighter.registerType()

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