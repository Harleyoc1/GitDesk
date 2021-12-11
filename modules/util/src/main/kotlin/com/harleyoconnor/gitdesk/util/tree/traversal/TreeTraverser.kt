package com.harleyoconnor.gitdesk.util.tree.traversal

import com.harleyoconnor.gitdesk.util.tree.Tree

/**
 *
 * @author Harley O'Connor
 */
interface TreeTraverser<E> {

    fun traverse(node: Tree.Node<E>)

}