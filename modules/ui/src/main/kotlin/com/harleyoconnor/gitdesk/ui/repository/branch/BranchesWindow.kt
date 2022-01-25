package com.harleyoconnor.gitdesk.ui.repository.branch

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.git.repository.Branch
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.repository.RepositoryWindow
import com.harleyoconnor.gitdesk.ui.style.Stylesheet
import com.harleyoconnor.gitdesk.ui.style.Stylesheets
import com.harleyoconnor.gitdesk.ui.window.AbstractWindow
import javafx.scene.layout.Region
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class BranchesWindow(val parent: RepositoryWindow, val repository: LocalRepository) :
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
        openMainView()
    }

    fun openMainView() {
        root = BranchesController.Loader.load(BranchesController.Context(this, repository)).root
    }

    fun openAddView() {
        root = CreateBranchController.Loader.load(
            CreateBranchController.Context(this, repository.gitRepository)
        ).root
    }

    fun openEditView(branch: Branch) {
        root = EditBranchController.Loader.load(
            EditBranchController.Context(this, repository.gitRepository, branch)
        ).root
    }

    fun refreshRepositoryWindow() {
        parent.refreshView()
    }

    override fun open() {
        openMainView()
        super.open()
    }

    override fun closeAndSaveResources() {
    }
}