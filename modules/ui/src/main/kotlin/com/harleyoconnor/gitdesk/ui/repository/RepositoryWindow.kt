package com.harleyoconnor.gitdesk.ui.repository

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.style.StaticStylesheet
import com.harleyoconnor.gitdesk.ui.style.Stylesheet
import com.harleyoconnor.gitdesk.ui.style.Stylesheets
import com.harleyoconnor.gitdesk.ui.style.ThemedStylesheet
import com.harleyoconnor.gitdesk.ui.window.AbstractWindow
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class RepositoryWindow(
    stage: Stage, private val repository: LocalRepository
) : AbstractWindow(stage, RepositoryController.load(stage, repository), Application.getInstance().windowManager) {

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

    override val stylesheets: Array<Stylesheet> get() = arrayOf(
        Stylesheets.DEFAULT, Stylesheets.DEFAULT_THEMED, Stylesheets.REPOSITORY
    )

    override fun stop() {
        Data.repositoryAccess.save(repository.directory, repository)
    }
}