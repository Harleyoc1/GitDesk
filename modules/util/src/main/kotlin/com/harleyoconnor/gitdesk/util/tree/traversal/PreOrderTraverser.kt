package com.harleyoconnor.gitdesk.util.tree.traversal

import com.harleyoconnor.gitdesk.util.tree.Tree

/**
 * A tree traverser which visits the given node and then any child nodes, recursively.
 *
 * @author Harley O'Connor
 */
class PreOrderTraverser<E> private constructor(visitor: NodeVisitor<E>) : AbstractTreeTraverser<E>(visitor) {

    companion object {
        internal fun <E> traverseNodes(visitor: NodeVisitor<E>): TreeTraverser<E> =
            PreOrderTraverser(visitor)

        fun <E> traverse(visitor: ElementVisitor<E>): TreeTraverser<E> =
            traverseNodes { visitor(it.get()) }
    }

    override fun traverse(node: Tree.Node<E>) {
        this.visit(node)
        if (node is Tree.ParentNode) {
            this.visitChildren(node)
        }
    }

    private fun visitChildren(node: Tree.ParentNode<E>) {
        node.getChildNodes().forEach {
            this.traverse(it)
        }
    }
}