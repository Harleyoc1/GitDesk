package com.harleyoconnor.gitdesk.ui.menu

import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.menubar.SelectableAccess
import com.harleyoconnor.gitdesk.ui.window.AbstractWindow
import javafx.scene.layout.Region
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class MenuWindow(
    stage: Stage
) : AbstractWindow(stage, Region(), Application.getInstance().windowManager) {

    override val minWidth: Double get() = 550.0
    override val minHeight: Double get() = 400.0

    override val id: String get() = "Menu"

    val selectableAccess: SelectableAccess = SelectableAccess(scene)

    init {
        root = MenuController.load(this)
    }

    override fun postClose() {
        if (manager.noWindowsOpen()) {
            Application.getInstance().close()
        }
    }

    override fun closeAndSaveResources() {
    }
}