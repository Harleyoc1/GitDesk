package com.harleyoconnor.gitdesk.util.tree.traversal

import com.harleyoconnor.gitdesk.util.tree.Tree

typealias ElementVisitor<E> = (E) -> Unit
typealias NodeVisitor<E> = (Tree.Node<E>) -> Unit

/**
 * @author Harley O'Connor
 */
abstract class AbstractTreeTraverser<E>(
    private val visitor: ElementVisitor<E>,
    private val nodeVisitor: NodeVisitor<E>
) : TreeTraverser<E> {

    protected fun visit(node: Tree.Node<E>) {
        this.visitor(node.get())
        this.nodeVisitor(node)
    }

}