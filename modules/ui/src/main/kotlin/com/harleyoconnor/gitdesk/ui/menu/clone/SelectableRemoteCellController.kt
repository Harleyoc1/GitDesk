package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import javafx.fxml.FXML
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox

/**
 * @author Harley O'Connor
 */
class SelectableRemoteCellController: RemoteCellController<SelectableRemoteCellController.Context>() {

    object Loader : ResourceViewLoader<Context, SelectableRemoteCellController, HBox>(
        UIResource("/ui/layouts/menu/clone/SelectableRemoteCell.fxml")
    )

    class Context(val parent: CloneTab, remote: Remote) : RemoteCellController.Context(remote)

    private lateinit var parent: CloneTab

    @FXML
    private fun mousePressed(event: MouseEvent) {
        if (event.button == MouseButton.PRIMARY) {
            toLocationSelection()
        }
    }

    override fun setup(context: Context) {
        super.setup(context)
        this.parent = context.parent
    }

    @FXML
    private fun toLocationSelection() {
        parent.toLocationSelection(remote)
    }

}