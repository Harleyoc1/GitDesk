package com.harleyoconnor.gitdesk.ui.repository.ignored

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.git.repository.GitIgnoreFile
import com.harleyoconnor.gitdesk.git.repository.IgnoreFile
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.repository.RepositoryWindow
import com.harleyoconnor.gitdesk.ui.style.Stylesheet
import com.harleyoconnor.gitdesk.ui.style.Stylesheets
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.window.AbstractWindow
import javafx.scene.layout.Region
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class IgnoredWindow(val parent: RepositoryWindow, val repository: LocalRepository) :
    AbstractWindow(Stage(), Region(), Application.getInstance().windowManager) {

    override val minWidth: Double
        get() = 400.0
    override val minHeight: Double
        get() = 300.0
    override val id: String
        get() = "Ignored"

    override val title: String get() = TRANSLATIONS_BUNDLE.getString("window.ignored.title")

    override fun getStylesheets(): Array<Stylesheet> = arrayOf(
        Stylesheets.DEFAULT_THEMED, Stylesheets.DEFAULT, Stylesheets.REPOSITORY_THEMED, Stylesheets.REPOSITORY
    )

    private val ignoreFile: IgnoreFile = GitIgnoreFile(
        repository.gitRepository, Application.getInstance().backgroundExecutor
    )

    private fun openMainView() {
        root = IgnoredController.Loader.load(
            IgnoredController.Context(
                ignoreFile,
                this::openAddView,
                this::openEditView
            )
        ).root
    }

    private fun openAddView(rule: IgnoreFile.Rule) {
        root = EditRuleController.Loader.load(
            EditRuleController.Context(
                rule, { newBody ->
                    ignoreFile.add(newBody)
                    openMainView()
                }, this::openMainView
            )
        ).root
    }

    private fun openEditView(rule: IgnoreFile.Rule) {
        root = EditRuleController.Loader.load(
            EditRuleController.Context(
                rule, { newBody ->
                    ignoreFile.edit(rule, newBody)
                    openMainView()
                }, this::openMainView
            )
        ).root
    }

    override fun open() {
        openMainView()
        super.open()
    }

    override fun closeAndSaveResources() {
    }
}