package com.harleyoconnor.gitdesk.ui

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.ui.account.AccountWindow
import com.harleyoconnor.gitdesk.ui.menu.MenuWindow
import com.harleyoconnor.gitdesk.ui.repository.RepositoryWindow
import com.harleyoconnor.gitdesk.ui.settings.AppSettings
import com.harleyoconnor.gitdesk.ui.window.SetWindowManager
import javafx.application.Platform
import javafx.stage.Stage
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
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

    val mainThreadExecutor = Executor { command ->
        Platform.runLater {
            command.run()
        }
    }

    val backgroundExecutor: ExecutorService = Executors.newCachedThreadPool()

    fun getPrimaryStage(): Stage = primaryStage

    init {
        INSTANCE = this
        AppSettings.load()
    }

    override fun start(primaryStage: Stage) {
        this.primaryStage = primaryStage

        openLastOpenRepositories()

        if (windowManager.noWindowsOpen()) {
            MenuWindow(primaryStage).open()
            AccountWindow(Stage()).open() // TODO: Proper way of accessing account window.
        }
    }

    private fun openLastOpenRepositories() {
        Data.repositoryAccess.getAll().entries.stream()
            .map { Data.repositoryAccess.get(it.key) }
            .filter { it.open }
            .forEach {
                RepositoryWindow.focusOrOpen(it)
            }
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