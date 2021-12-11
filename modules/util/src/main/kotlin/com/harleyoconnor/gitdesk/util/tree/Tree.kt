package com.harleyoconnor.gitdesk.util.tree

import com.harleyoconnor.gitdesk.util.tree.traversal.TreeTraverser

/**
 *
 * @author Harley O'Connor
 */
// TODO: Fully encapsulated Tree impls.
interface Tree<E> {

    fun root(): ParentNode<E>

    fun traverse(traverser: TreeTraverser<E>)

    fun mutable(): MutableTree<E>

    fun immutable(): Tree<E>

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    fun toArray(): Array<E>

    interface Node<E> {
        fun get(): E
        fun parent(): ParentNode<E>?
    }

    interface ParentNode<E> : Node<E> {
        override fun parent(): ParentNode<E>?
        fun getChildNodes(): Array<Node<E>>
    }

    interface LeafNode<E> : Node<E> {
        override fun parent(): ParentNode<E>
    }

}