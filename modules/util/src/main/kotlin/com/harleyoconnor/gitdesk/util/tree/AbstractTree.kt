package com.harleyoconnor.gitdesk.util.tree

import com.harleyoconnor.gitdesk.util.tree.traversal.PreOrderTraverser
import com.harleyoconnor.gitdesk.util.tree.traversal.TreeTraverser

/**
 *
 * @author Harley O'Connor
 */
abstract class AbstractTree<E> : Tree<E> {

    override fun traverse(traverser: TreeTraverser<E>) {
        traverser.traverse(this.root())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Tree<*>) return false

        return this.toArray().contentEquals(other.toArray())
    }

    override fun hashCode(): Int {
        return this.toArray().hashCode()
    }

    @Suppress("UNCHECKED_CAST")
    override fun toArray(): Array<E> {
        val contents = mutableListOf<E>()
        this.traverse(PreOrderTraverser.traverse {
            contents.add(it)
        })
        return contents.stream().toArray { arrayOfNulls<Any>(contents.size) } as Array<E>
    }

}