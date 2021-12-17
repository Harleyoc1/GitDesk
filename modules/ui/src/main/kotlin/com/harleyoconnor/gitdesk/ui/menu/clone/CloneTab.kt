package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.util.Tab
import javafx.scene.Node
import javafx.scene.layout.Region
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class CloneTab(
    val stage: Stage, setter: (Node) -> Unit
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