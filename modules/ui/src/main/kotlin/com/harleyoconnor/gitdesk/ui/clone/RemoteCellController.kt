package com.harleyoconnor.gitdesk.ui.clone

import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.menu.clone.RemoteCellController.Context
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.util.getIcon
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import javafx.fxml.FXML
import javafx.scene.layout.HBox

/**
 * @author Harley O'Connor
 */
class RemoteCellController: com.harleyoconnor.gitdesk.ui.menu.clone.RemoteCellController<Context>() {

    object Loader : ResourceViewLoader<Context, RemoteCellController, HBox>(
        UIResource("/ui/layouts/clone/RemoteCell.fxml")
    )

    @FXML
    private lateinit var platformIcon: SVGIcon

    override fun setup(context: Context) {
        super.setup(context)
        platformIcon.setupFromSvg(remote.getIcon())
    }

}