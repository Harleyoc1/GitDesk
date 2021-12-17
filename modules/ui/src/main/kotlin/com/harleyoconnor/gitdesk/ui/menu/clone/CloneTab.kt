package com.harleyoconnor.gitdesk.ui.menu.clone

import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.ui.menu.MenuController
import javafx.scene.Node
import javafx.scene.layout.Region
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class CloneTab(
    val stage: Stage, appender: (Node) -> Unit
) : MenuController.Tab(Region(), appender) {

    private val remoteSelectionView = SelectRemoteTabController.load(this)

    init {
        this.node = remoteSelectionView
    }

    fun returnToRemoteSelection() {
        appender(remoteSelectionView)
    }

    fun toLocationSelection(remote: Remote) {
        appender(SelectLocationController.load(remote, this))
    }

}