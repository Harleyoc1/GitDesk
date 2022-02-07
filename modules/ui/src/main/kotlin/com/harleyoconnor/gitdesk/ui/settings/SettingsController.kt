package com.harleyoconnor.gitdesk.ui.settings

import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.util.Tab
import com.harleyoconnor.gitdesk.ui.util.setOnSelected
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.RadioButton
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class SettingsController : ViewController<SettingsController.Context> {

    object Loader : ResourceViewLoader<Context, SettingsController, BorderPane>(
        UIResource("/ui/layouts/settings/Settings.fxml")
    )

    class Context(val stage: Stage, val closeCallback: () -> Unit) : ViewController.Context

    private val settings = AppSettings.get().getOrLoad().copy()

    private lateinit var stage: Stage
    private lateinit var closeCallback: () -> Unit

    @FXML
    private lateinit var root: BorderPane

    @FXML
    private lateinit var appearanceTabButton: RadioButton

    @FXML
    private lateinit var integrationsTabButton: RadioButton

    @FXML
    private lateinit var repositoriesTabButton: RadioButton


    private val appearanceTab: Tab by lazy {
        Tab(
            AppearanceTabController.Loader.load(AppearanceTabController.Context(settings, closeCallback)).root,
            this::openTab
        )
    }

    private val integrationsTab: Tab by lazy {
        Tab(
            IntegrationsTabController.Loader.load(
                IntegrationsTabController.Context(
                    settings,
                    stage,
                    closeCallback
                )
            ).root,
            this::openTab
        )
    }

    private val repositoriesTab: Tab by lazy {
        Tab(
            RepositoriesTabController.Loader.load(RepositoriesTabController.Context(settings, closeCallback)).root,
            this::openTab
        )
    }

    @FXML
    private fun initialize() {
        appearanceTabButton.setOnSelected {
            appearanceTab.open()
        }
        integrationsTabButton.setOnSelected {
            integrationsTab.open()
        }
        repositoriesTabButton.setOnSelected {
            repositoriesTab.open()
        }
    }

    private fun openTab(node: Node) {
        this.root.center = node
    }

    override fun setup(context: Context) {
        stage = context.stage
        closeCallback = context.closeCallback
        appearanceTabButton.fire()
    }

}