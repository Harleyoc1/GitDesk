package com.harleyoconnor.gitdesk.ui.menubar

import javafx.scene.Scene

/**
 *
 * @author Harley O'Connor
 */
class SelectableAccess(private val scene: Scene) {

    fun getCurrent(): Selectable? {
        return Selectable.forNode(scene.focusOwner)
    }

}