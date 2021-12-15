package com.harleyoconnor.gitdesk.ui.menu

import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.util.loadLayout
import com.harleyoconnor.gitdesk.ui.window.AbstractWindow
import com.harleyoconnor.gitdesk.ui.window.WindowManager
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class MenuWindow(
    stage: Stage, manager: WindowManager
) : AbstractWindow(stage, MenuController.load(stage), manager) {

    override val minWidth: Double get() = 550.0
    override val minHeight: Double get() = 400.0

    override val id: String get() = "Menu"

    override fun postClose() {
        if (manager.noWindowsOpen()) {
            Application.getInstance().close()
        }
    }
}