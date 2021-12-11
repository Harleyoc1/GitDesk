package com.harleyoconnor.gitdesk.util.tree

/**
 *
 * @author Harley O'Connor
 */
class ArrayTree<E>(private val root: Tree.ParentNode<E>) : AbstractTree<E>() {

    override fun root(): Tree.ParentNode<E> {
        return this.root
    }

    override fun mutable(): MutableTree<E> {
        val tree = MutableArrayTree(this.root.get())
        this.root.getChildNodes().forEach {
            this.addNode(it, tree.root())
        }
        return tree
    }

    private fun addNode(
        node: Tree.Node<E>,
        to: MutableTree.ParentNode<E>
    ) {
        if (node is Tree.ParentNode) {
            this.addNodes(node, to)
        } else {
            to.addLeaf(node.get())
        }
    }

    private fun addNodes(from: Tree.ParentNode<E>, to: MutableTree.ParentNode<E>) {
        val node = to.addParent(from.get())
        from.getChildNodes().forEach {
            this.addNode(it, node)
        }
    }

    override fun immutable(): Tree<E> {
        return this
    }

    class ParentNode<E>(
        private val element: E,
        private val parent: ParentNode<E>?,
        private val children: Array<Tree.Node<E>>
    ) : Tree.ParentNode<E> {

        override fun get(): E {
            return element
        }

        override fun parent(): Tree.ParentNode<E>? {
            return this.parent
        }

        override fun getChildNodes(): Array<Tree.Node<E>> {
            return this.children.copyOf()
        }
    }

    class LeafNode<E>(
        private val element: E,
        private val parent: ParentNode<E>
    ) : Tree.LeafNode<E> {

        override fun get(): E {
            return this.element
        }

        override fun parent(): Tree.ParentNode<E> {
            return this.parent
        }

    }

}