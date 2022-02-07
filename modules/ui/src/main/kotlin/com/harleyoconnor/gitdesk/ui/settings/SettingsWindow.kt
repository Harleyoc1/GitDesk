package com.harleyoconnor.gitdesk.ui.settings

import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.window.AbstractWindow
import javafx.scene.layout.Region
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class SettingsWindow : AbstractWindow(
    Stage(),
    Region(),
    Application.getInstance().windowManager
) {

    override val minWidth: Double get() = 500.0
    override val minHeight: Double get() = 400.0
    override val id: String get() = "Settings"

    override val title: String get() = TRANSLATIONS_BUNDLE.getString("window.settings.title")

    init {
        root = SettingsController.Loader.load(
            SettingsController.Context(stage) { this.close() }
        ).root
    }

    override fun closeAndSaveResources() {
    }


}