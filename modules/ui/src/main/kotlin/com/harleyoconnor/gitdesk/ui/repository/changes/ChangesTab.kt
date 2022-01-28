package com.harleyoconnor.gitdesk.ui.repository.changes

import com.harleyoconnor.gitdesk.data.local.LocalRepository
import com.harleyoconnor.gitdesk.ui.util.Tab
import javafx.scene.Node
import javafx.scene.layout.Region
import javafx.stage.Stage

/**
 *
 * @author Harley O'Connor
 */
class ChangesTab(
    stage: Stage,
    repository: LocalRepository,
    setter: (Node) -> Unit
): Tab(Region(), setter) {

    private val changesTabView = ChangesTabController.Loader.load(
        ChangesTabController.Context(stage, repository)
    )

    init {
        node = changesTabView.root
    }

    override fun open() {
        refresh()
        super.open()
    }

    fun refresh() {
        changesTabView.controller.refresh()
    }
}