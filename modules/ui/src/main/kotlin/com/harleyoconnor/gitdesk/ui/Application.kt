package com.harleyoconnor.gitdesk.ui

import com.harleyoconnor.gitdesk.ui.menu.MenuWindow
import com.harleyoconnor.gitdesk.ui.window.SetWindowManager
import javafx.application.Platform
import javafx.stage.Stage
import java.util.concurrent.Executors
import kotlin.system.exitProcess

/**
 * @author Harley O'Connor
 */
class Application : javafx.application.Application() {

    companion object {
        private lateinit var INSTANCE: Application

        fun getInstance(): Application = INSTANCE
    }

    private lateinit var primaryStage: Stage

    val windowManager = SetWindowManager()

    val backgroundExecutor = Executors.newSingleThreadExecutor()

    fun getPrimaryStage(): Stage = primaryStage

    init {
        INSTANCE = this
    }

    override fun start(primaryStage: Stage) {
        this.primaryStage = primaryStage

        MenuWindow(primaryStage, windowManager).open()
    }

    override fun stop() {
        windowManager.forEach {
            it.closeAndSaveResources()
        }
    }

    fun close() {
        this.stop()
        Platform.exit()
        exitProcess(0)
    }

}