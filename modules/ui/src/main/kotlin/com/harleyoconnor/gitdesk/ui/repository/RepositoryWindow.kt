package com.harleyoconnor.gitdesk.ui.repository

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.data.WindowCache
import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.repository.branch.BranchesWindow
import com.harleyoconnor.gitdesk.ui.repository.ignored.IgnoredWindow
import com.harleyoconnor.gitdesk.ui.style.Stylesheet
import com.harleyoconnor.gitdesk.ui.style.Stylesheets
import com.harleyoconnor.gitdesk.ui.view.ViewLoader
import com.harleyoconnor.gitdesk.ui.window.AbstractWindow
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Region
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class RepositoryWindow(
    stage: Stage, val repository: LocalRepository
) : AbstractWindow(stage, Region(), Application.getInstance().windowManager) {

    companion object {
        fun focusOrOpen(repository: LocalRepository) {
            val window =
                (Application.getInstance().windowManager.get(repository.id) ?: RepositoryWindow(Stage(), repository))
            window.open()
            window.focus()
        }
    }

    override val minWidth: Double get() = 600.0
    override val minHeight: Double get() = 450.0

    override val id: String get() = repository.id

    private val branchesWindow by lazy { BranchesWindow(this, repository) }

    private val ignoredWindow by lazy { IgnoredWindow(this, repository) }

    override var title: String = ""
        set(value) {
            field = value
            stage.title = value
        }

    override fun getStylesheets(): Array<Stylesheet> = arrayOf(
        Stylesheets.DEFAULT_THEMED, Stylesheets.DEFAULT, Stylesheets.REPOSITORY_THEMED, Stylesheets.REPOSITORY
    )

    private var rootView: ViewLoader.View<RepositoryController, BorderPane> =
        RepositoryController.Loader.load(RepositoryController.Context(this, repository))
        set(value) { field = value; root = value.root }

    init {
        title = repository.id
        root = rootView.root
        loadFromWindowCache(repository.windowCache)
    }

    private fun loadFromWindowCache(windowCache: WindowCache) {
        this.stage.x = windowCache.x
        this.stage.y = windowCache.y
        this.stage.width = windowCache.width
        this.stage.height = windowCache.height
        this.stage.isFullScreen = windowCache.fullScreen
    }

    fun refreshView() {
        closeAndSaveResources()
        rootView = RepositoryController.Loader.load(RepositoryController.Context(this, repository))
    }

    override fun open() {
        super.open()
        this.repository.open = true
    }

    override fun postClose() {
        branchesWindow.close()
        this.repository.close()
        super.postClose()
    }

    override fun closeAndSaveResources() {
        saveToWindowCache()
        Data.repositoryAccess.save(repository.directory, repository)
    }

    override fun onCloseAppRequested() {
        // If close was requested from here repository was still open.
        this.repository.open = true
        this.closeAndSaveResources()
        super.onCloseAppRequested()
    }

    private fun saveToWindowCache() {
        this.repository.windowCache = WindowCache(
            this.stage.x,
            this.stage.y,
            this.stage.width,
            this.stage.height,
            this.stage.isFullScreen
        )
    }

    fun openBranchesWindow() {
        branchesWindow.open()
        branchesWindow.focus() // In case window was already open, make sure to bring it to the foreground.
    }

    fun openNewBranchWindow() {
        openBranchesWindow()
        branchesWindow.openAddView()
    }

    fun openIgnoredWindow() {
        ignoredWindow.open()
        ignoredWindow.focus() // In case window was already open, make sure to bring it to the foreground.
    }

    fun promptCommit() {
        rootView.controller.promptCommit()
    }

    fun getCurrentRemote(): RemoteContext {
        return rootView.controller.remoteContext
    }

}