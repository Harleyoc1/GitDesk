package com.harleyoconnor.gitdesk.util.tree

import com.harleyoconnor.gitdesk.util.tree.traversal.PreOrderTraverser

/**
 *
 * @author Harley O'Connor
 */
class MutableArrayTree<E> : AbstractTree<E>, MutableTree<E> {

    constructor(element: E) : super() {
        this.root = ParentNode(element, null)
    }

    constructor(root: ParentNode<E>) : super() {
        this.root = root
    }

    private val root: ParentNode<E>

    override fun root(): MutableTree.ParentNode<E> {
        return this.root
    }

    override fun addLeaf(parent: E, element: E): Boolean {
        var added = false
        this.traverse(PreOrderTraverser.traverseNodes {
            if (it.get() == parent && this.addChild(it, element)) {
                added = true
            }
        })
        return added
    }

    private fun addChild(leaf: Tree.Node<E>, element: E): Boolean {
        val parent = if (leaf !is ParentNode) {
            (leaf as? LeafNode)?.toParent()
        } else leaf
        return parent?.addLeaf(element) != null
    }

    override fun remove(element: E): Boolean {
        if (this.root().get() == element) {
            throw IllegalArgumentException("Cannot remove root node.")
        }
        var removed = false
        this.traverse(PreOrderTraverser.traverseNodes {
            if (it.get() == element && this.removeNode(it)) {
                removed = true
            }
        })
        return removed
    }

    private fun removeNode(node: Tree.Node<E>): Boolean {
        return (node.parent() as? ParentNode<E>)?.removeChild(node.get()) != null
    }

    override fun immutable(): Tree<E> {
        return ArrayTree(this.root) // TODO: Copy of root.
    }

    class ParentNode<E>(
        private var element: E,
        private val parent: ParentNode<E>?,
        private val children: MutableList<Tree.Node<E>> = mutableListOf()
    ) : MutableTree.ParentNode<E> {

        override fun get(): E {
            return element
        }

        override fun set(element: E) {
            this.element = element
        }

        override fun parent(): ParentNode<E>? {
            return this.parent
        }

        override fun getChildNodes(): Array<Tree.Node<E>> {
            return this.children.toTypedArray()
        }

        override fun addParent(element: E): ParentNode<E> {
            val node = ParentNode(element, this)
            this.children.add(node)
            return node
        }

        override fun addLeaf(element: E): ParentNode<E> {
            val node = LeafNode(element, this)
            this.children.add(node)
            return this
        }

        override fun removeChild(element: E) {
            this.children.removeIf { it.get() == element }
        }

        override fun addChildNodes(vararg nodes: Tree.Node<E>) {
            nodes.forEach {
                assert(it is ParentNode || it is LeafNode) // TODO: AHHHHHHH
                this.children.add(it)
            }
        }

        override fun clear() {
            this.children.clear()
        }
    }

    class LeafNode<E>(
        private var element: E,
        private val parent: ParentNode<E>
    ) : MutableTree.LeafNode<E> {

        override fun get(): E {
            return this.element
        }

        override fun set(element: E) {
            this.element = element
        }

        override fun parent(): ParentNode<E> {
            return this.parent
        }

        override fun toParent(): ParentNode<E> {
            this.parent.removeChild(this.element)
            return this.parent.addParent(this.element)
        }
    }

}