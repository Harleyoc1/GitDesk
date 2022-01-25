package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.menu.MenuWindow
import com.harleyoconnor.gitdesk.ui.util.Tab
import com.harleyoconnor.gitdesk.ui.window.Window
import javafx.scene.Node
import javafx.scene.layout.Region
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class CloneTab(
    val window: MenuWindow, setter: (Node) -> Unit
) : Tab(Region(), setter) {

    private val remoteSelectionView = SelectRemoteTabController.Loader.load(
        SelectRemoteTabController.Context(this)
    )

    init {
        this.node = remoteSelectionView.root
    }

    fun returnToRemoteSelection() {
        this.node = remoteSelectionView.root
    }

    fun toLocationSelection(remote: Remote) {
        this.node = SelectLocationController.Loader.load(SelectLocationController.Context(this, remote)).root
    }

}