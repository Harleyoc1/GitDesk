package com.harleyoconnor.gitdesk.ui.repository

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.data.WindowCache
import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.style.Stylesheet
import com.harleyoconnor.gitdesk.ui.style.Stylesheets
import com.harleyoconnor.gitdesk.ui.window.AbstractWindow
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

    override fun getStylesheets(): Array<Stylesheet> = arrayOf(
        Stylesheets.DEFAULT_THEMED, Stylesheets.DEFAULT, Stylesheets.REPOSITORY_THEMED, Stylesheets.REPOSITORY
    )

    init {
        root = RepositoryController.load(this, repository)
        loadFromWindowCache(repository.windowCache)
    }

    private fun loadFromWindowCache(windowCache: WindowCache) {
        this.stage.x = windowCache.x
        this.stage.y = windowCache.y
        this.stage.width = windowCache.width
        this.stage.height = windowCache.height
        this.stage.isFullScreen = windowCache.fullScreen
    }

    override fun open() {
        root = RepositoryController.load(this, repository)
        super.open()
        this.repository.open = true
    }

    override fun postClose() {
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

}