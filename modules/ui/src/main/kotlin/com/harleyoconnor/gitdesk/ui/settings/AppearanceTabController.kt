package com.harleyoconnor.gitdesk.ui.settings

import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class AppearanceTabController : ViewController<AppearanceTabController.Context> {

    object Loader: ResourceViewLoader<Context, AppearanceTabController, VBox>(
        UIResource("/ui/layouts/settings/AppearanceTab.fxml")
    )

    class Context(val settings: AppSettings, val closeCallback: () -> Unit): ViewController.Context

    private lateinit var settings: AppSettings
    private lateinit var closeCallback: () -> Unit

    @FXML
    private lateinit var themeChoiceBox: ChoiceBox<AppSettings.Appearance.ThemeSelection>

    @FXML
    private lateinit var saveButton: Button

    @FXML
    private fun initialize() {
        themeChoiceBox.items.addAll(AppSettings.Appearance.ThemeSelection.values())
        themeChoiceBox.selectionModel.selectedItemProperty().addListener { _, _, new ->
            settings.appearance.theme = new
        }
    }

    override fun setup(context: Context) {
        settings = context.settings
        closeCallback = context.closeCallback

        themeChoiceBox.selectionModel.select(settings.appearance.theme)
    }

    @FXML
    private fun cancel(event: ActionEvent) {
        closeCallback()
    }

    @FXML
    private fun save(event: ActionEvent) {
        AppSettings.get().save(settings)
        closeCallback()
    }

    @FXML
    private fun keyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            saveButton.fire()
        }
    }

}