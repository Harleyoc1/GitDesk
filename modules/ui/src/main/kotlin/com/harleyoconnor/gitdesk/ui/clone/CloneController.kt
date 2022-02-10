package com.harleyoconnor.gitdesk.ui.clone

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.git.clone
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.menu.clone.CloneWindow
import com.harleyoconnor.gitdesk.ui.repository.RepositoryWindow
import com.harleyoconnor.gitdesk.ui.settings.AppSettings
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.process.logFailure
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.layout.VBox

/**
 *
 * @author Harley O'Connor
 */
class CloneController : ViewController<CloneController.Context> {

    object Loader : ResourceViewLoader<Context, CloneController, VBox>(
        UIResource("/ui/layouts/clone/Clone.fxml")
    )

    class Context(val parent: CloneWindow) : ViewController.Context

    private lateinit var parent: CloneWindow

    @FXML
    private lateinit var root: VBox

    override fun setup(context: Context) {
        this.parent = context.parent
        this.root.children.add(
            1,
            RemoteCellController.Loader.load(
                com.harleyoconnor.gitdesk.ui.menu.clone.RemoteCellController.Context(parent.remote)
            ).root
        )
        clone(parent.remote.url, parent.destination)
            .ifSuccessful {
                queueOpenRepository()
            }
            .ifFailure(::logFailure)
            .begin()
    }

    private fun queueOpenRepository() {
        Platform.runLater {
            RepositoryWindow.focusOrOpen(
                LocalRepository(
                    parent.destination.name,
                    parent.destination,
                    null,
                    AppSettings.get().getOrLoad().repositories.showHiddenFilesByDefault
                )
            )
            parent.close()
        }
    }

}