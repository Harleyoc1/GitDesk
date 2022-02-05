package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.data.remote.withFullData
import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.clone.CloneController
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
import com.harleyoconnor.gitdesk.ui.window.AbstractWindow
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.toGitDisplayUrl
import javafx.scene.layout.Region
import javafx.stage.Stage

/**
 *
 * @author Harley O'Connor
 */
class CloneWindow(val remote: Remote, val destination: Directory) :
    AbstractWindow(Stage(), Region(), Application.getInstance().windowManager) {

    override val minWidth: Double
        get() = 300.0
    override val minHeight: Double
        get() = 145.0
    override val id: String
        get() = "Clone"

    override var title: String = TRANSLATIONS_BUNDLE.getString("window.clone.title")
        set(value) {
            field = value
            stage.title = value
        }

    init {
        this.title = TRANSLATIONS_BUNDLE.getString(
            "window.clone.title",
            remote.withFullData()?.name?.getFullName() ?: remote.url.toGitDisplayUrl()
        )
        this.root = CloneController.Loader.load(CloneController.Context(this)).root
    }

    override fun closeAndSaveResources() {
    }

}