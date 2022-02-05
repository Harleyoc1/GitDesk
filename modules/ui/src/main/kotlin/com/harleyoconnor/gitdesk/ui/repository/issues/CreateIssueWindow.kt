package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.style.Stylesheet
import com.harleyoconnor.gitdesk.ui.style.Stylesheets
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
import com.harleyoconnor.gitdesk.ui.window.AbstractWindow
import javafx.scene.layout.Region
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class CreateIssueWindow(
    val remoteContext: RemoteContext,
    val createdCallback: (Issue) -> Unit
) : AbstractWindow(Stage(), Region(), Application.getInstance().windowManager) {

    override val minWidth: Double get() = 300.0
    override val minHeight: Double get() = 200.0
    override val id: String get() = "CreateIssue"

    override var title: String = TRANSLATIONS_BUNDLE.getString("window.create_issue.title")
        set(value) {
            field = value
            stage.title = value
        }

    override fun getStylesheets(): Array<Stylesheet> = arrayOf(
        Stylesheets.DEFAULT_THEMED, Stylesheets.DEFAULT, Stylesheets.REPOSITORY_THEMED, Stylesheets.REPOSITORY
    )

    init {
        this.title = TRANSLATIONS_BUNDLE.getString(
            "window.create_issue.title", remoteContext.remote.name.getFullName()
        )
        root = CreateIssueController.Loader.load(
            CreateIssueController.Context(
                remoteContext,
                { close(); createdCallback(it) },
                { close() }
            )
        ).root
    }

    override fun closeAndSaveResources() {
    }

}