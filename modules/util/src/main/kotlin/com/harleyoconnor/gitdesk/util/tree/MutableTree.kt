package com.harleyoconnor.gitdesk.util.tree

/**
 *
 * @author Harley O'Connor
 */
interface MutableTree<E> : Tree<E> {

    override fun root(): ParentNode<E>

    override fun mutable(): MutableTree<E> {
        return this
    }

    fun addLeaf(parent: E, element: E): Boolean

    fun remove(element: E): Boolean

    interface ParentNode<E> : Tree.ParentNode<E> {
        override fun parent(): ParentNode<E>?
        fun set(element: E)
        fun addParent(element: E): ParentNode<E>
        fun addLeaf(element: E): ParentNode<E>
        fun removeChild(element: E)
        fun addChildNodes(vararg nodes: Tree.Node<E>)
        fun clear()
    }

    interface LeafNode<E> : Tree.LeafNode<E> {
        fun set(element: E)
        override fun parent(): ParentNode<E>
        fun toParent(): ParentNode<E>
    }

}