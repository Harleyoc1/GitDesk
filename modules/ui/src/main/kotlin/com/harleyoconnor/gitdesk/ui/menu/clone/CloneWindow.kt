package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.clone.CloneController
import com.harleyoconnor.gitdesk.ui.util.loadLayout
import com.harleyoconnor.gitdesk.ui.window.AbstractWindow
import com.harleyoconnor.gitdesk.util.Directory
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

    init {
        this.root = CloneController.setup(this)
    }

    override fun closeAndSaveResources() {
    }

}