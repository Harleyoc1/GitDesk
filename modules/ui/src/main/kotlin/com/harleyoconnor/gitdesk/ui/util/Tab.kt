package com.harleyoconnor.gitdesk.ui.util

import javafx.scene.Node

/**
 *
 * @author Harley O'Connor
 */
open class Tab(
    node: Node,
    protected val setter: (Node) -> Unit
) {
    open var node: Node = node
        protected set(value) {
            field = value
            open()
        }

    open fun open() = setter(node)
}