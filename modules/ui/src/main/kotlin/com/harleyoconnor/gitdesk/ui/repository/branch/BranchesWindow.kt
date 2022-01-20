package com.harleyoconnor.gitdesk.ui.repository.branch

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
class BranchesWindow(val repository: LocalRepository) :
    AbstractWindow(Stage(), Region(), Application.getInstance().windowManager) {

    override val minWidth: Double
        get() = 400.0
    override val minHeight: Double
        get() = 300.0
    override val id: String
        get() = "Branches"

    override fun getStylesheets(): Array<Stylesheet> = arrayOf(
        Stylesheets.DEFAULT_THEMED, Stylesheets.DEFAULT, Stylesheets.REPOSITORY_THEMED, Stylesheets.REPOSITORY
    )

    init {
        stage.isAlwaysOnTop = true
        root = BranchesController.load(repository)
    }

    override fun closeAndSaveResources() {
    }
}