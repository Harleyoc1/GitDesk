package com.harleyoconnor.gitdesk.ui.menu

import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.window.AbstractWindow
import javafx.scene.layout.Region
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class MenuWindow(
    stage: Stage
) : AbstractWindow(stage, Region(), Application.getInstance().windowManager) {

    companion object {
        private val ID = "Menu"

        fun showOrFocus(): MenuWindow {
            val window = Application.getInstance().windowManager.get(ID) ?: MenuWindow(Stage())
            window.open()
            window.focus()
            return window as MenuWindow
        }
    }

    override val minWidth: Double get() = 550.0
    override val minHeight: Double get() = 400.0

    override val id: String get() = ID

    override val title: String get() = TRANSLATIONS_BUNDLE.getString("window.menu.title")

    private val rootView = MenuController.Loader.load(MenuController.Context(this))

    init {
        root = rootView.root
    }

    fun selectCloneTab() {
        rootView.controller.selectCloneTab()
    }

    fun selectCreateTab() {
        rootView.controller.selectCreateTab()
    }

    fun selectImportLocal() {
        rootView.controller.selectImportLocal()
    }

    override fun postClose() {
        if (manager.noWindowsOpen()) {
            Application.getInstance().close()
        }
    }

    override fun closeAndSaveResources() {
    }
}