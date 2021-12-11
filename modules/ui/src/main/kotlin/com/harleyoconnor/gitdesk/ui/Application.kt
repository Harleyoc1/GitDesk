package com.harleyoconnor.gitdesk.ui

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.ui.menu.MenuWindow
import com.harleyoconnor.gitdesk.ui.window.SetWindowManager
import javafx.application.Platform
import javafx.stage.Stage
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
    private val windowManager = SetWindowManager()

    fun getPrimaryStage(): Stage = primaryStage

    init {
        INSTANCE = this
    }

    override fun start(primaryStage: Stage) {
        this.primaryStage = primaryStage

        MenuWindow(primaryStage, windowManager).open()
    }

    override fun stop() {
        Data.saveAllData()
    }

    fun close() {
        this.stop()
        Platform.exit()
        exitProcess(0)
    }

}