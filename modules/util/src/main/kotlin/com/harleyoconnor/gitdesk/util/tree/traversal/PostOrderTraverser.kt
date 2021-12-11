package com.harleyoconnor.gitdesk.util.tree.traversal

import com.harleyoconnor.gitdesk.util.tree.Tree

/**
 * A tree traverser that visits any child nodes recursively and then the given node.
 *
 * @author Harley O'Connor
 */
class PostOrderTraverser<E>(
    visitor: ElementVisitor<E>,
    nodeVisitor: NodeVisitor<E> = {}
) : AbstractTreeTraverser<E>(visitor, nodeVisitor) {

    override fun traverse(node: Tree.Node<E>) {
        if (node is Tree.ParentNode) {
            this.visitChildren(node)
        }
        this.visit(node)
    }

    private fun visitChildren(node: Tree.ParentNode<E>) {
        node.getChildNodes().forEach {
            this.traverse(it)
        }
    }
}