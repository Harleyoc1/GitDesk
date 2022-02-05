package com.harleyoconnor.gitdesk.ui.repository.editor

import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.style.Stylesheet
import com.harleyoconnor.gitdesk.ui.style.Stylesheets
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.window.AbstractWindow
import com.harleyoconnor.gitdesk.util.Directory
import javafx.scene.layout.Region
import javafx.stage.Stage
import java.io.File

/**
 * @author Harley O'Connor
 */
open class CreateFileWindow(
    val directory: Directory,
    val creationCallback: (File) -> Unit
) : AbstractWindow(Stage(), Region(), Application.getInstance().windowManager) {

    override val minWidth: Double get() = 300.0
    override val minHeight: Double get() = 160.0
    override val resizable: Boolean get() = false

    override val id: String get() = "CreateFile"

    override var title: String = TRANSLATIONS_BUNDLE.getString("window.create_file.title")

    override fun getStylesheets(): Array<Stylesheet> = arrayOf(
        Stylesheets.DEFAULT_THEMED, Stylesheets.DEFAULT, Stylesheets.REPOSITORY_THEMED, Stylesheets.REPOSITORY
    )

    init {
        stage.isAlwaysOnTop = true
        openView()
    }

    protected open fun openView() {
        root = CreateFileController.Loader.load(
            CreateFileController.Context(
                directory,
                { close(); creationCallback(it) },
                { close() }
            )
        ).root
    }

    override fun closeAndSaveResources() {
    }

}