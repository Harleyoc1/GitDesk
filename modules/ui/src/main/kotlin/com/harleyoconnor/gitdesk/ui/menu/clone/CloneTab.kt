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

    private val remoteSelectionView = SelectRemoteTabController.load(this)

    init {
        this.node = remoteSelectionView
    }

    fun returnToRemoteSelection() {
        this.node = remoteSelectionView
    }

    fun toLocationSelection(remote: Remote) {
        this.node = SelectLocationController.load(remote, this)
    }

}