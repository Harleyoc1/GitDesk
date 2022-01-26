package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.style.BOTTOM_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.style.TOP_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.util.getIcon
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import javafx.fxml.FXML
import javafx.scene.layout.HBox

/**
 * @author Harley O'Connor
 */
class SelectedRemoteCellController: RemoteCellController<SelectedRemoteCellController.Context>() {

    object Loader: ResourceViewLoader<Context, SelectedRemoteCellController, HBox>(
        UIResource("/ui/layouts/menu/tabs/clone/SelectedRemoteCell.fxml")
    )

    class Context(val parent: SelectLocationController, remote: Remote): RemoteCellController.Context(remote)

    private lateinit var parent: SelectLocationController

    @FXML
    private lateinit var platformIcon: SVGIcon

    @FXML
    override fun initialize() {
        root.pseudoClassStateChanged(TOP_PSEUDO_CLASS, true)
        root.pseudoClassStateChanged(BOTTOM_PSEUDO_CLASS, true)
    }

    override fun setup(context: Context) {
        super.setup(context)
        parent = context.parent
        platformIcon.setupFromSvg(remote.getIcon())
    }

    @FXML
    private fun editRemote() {
        parent.returnToRemoteSelection()
    }

}