package com.harleyoconnor.gitdesk.util.tree.traversal

import com.harleyoconnor.gitdesk.util.forEachAfter
import com.harleyoconnor.gitdesk.util.tree.Tree

/**
 * A tree traverser that visits the left-most child node first, then the given node, and finally the other child nodes.
 *
 * @author Harley O'Connor
 */
class InOrderTraverser<E> private constructor(visitor: NodeVisitor<E>) : AbstractTreeTraverser<E>(visitor) {

    companion object {
        internal fun <E> traverseNodes(visitor: NodeVisitor<E>): TreeTraverser<E> =
            InOrderTraverser(visitor)

        fun <E> traverse(visitor: ElementVisitor<E>): TreeTraverser<E> =
            traverseNodes { visitor(it.get()) }
    }

    override fun traverse(node: Tree.Node<E>) {
        if (node is Tree.ParentNode) {
            this.visitFirstChildAndSelf(node)
            this.visitOtherChildren(node)
        } else {
            this.visit(node)
        }
    }

    private fun visitFirstChildAndSelf(node: Tree.ParentNode<E>) {
        node.getChildNodes().firstOrNull()?.let {
            this.traverse(it)
        }
        this.visit(node)
    }

    private fun visitOtherChildren(node: Tree.ParentNode<E>) {
        node.getChildNodes().forEachAfter(0) {
            this.traverse(it)
        }
    }
}