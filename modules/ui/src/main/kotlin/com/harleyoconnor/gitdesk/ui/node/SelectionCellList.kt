package com.harleyoconnor.gitdesk.ui.node

import com.harleyoconnor.gitdesk.ui.style.SELECTED_PSEUDO_CLASS
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Node
import javafx.scene.layout.VBox

/**
 *
 * @author Harley O'Connor
 */
abstract class SelectionCellList<E> : VBox() {

    /** Currently displaying repositories. Indices equivalent to those of their cell in [content]'s children. */
    private val elements: MutableList<E> = mutableListOf()

    private var selection: Selection<E>? = null
        set(value) {
            field?.node?.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, false)
            field = value
            field?.node?.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, true)
        }

    private lateinit var onElementSelected: EventHandler<ElementSelectedEvent<E>>

    init {
        this.styleClass.add("selection-cell-list")
    }

    fun setOnElementSelected(handler: EventHandler<ElementSelectedEvent<E>>) {
        this.onElementSelected = handler
    }

    fun select() {
        selection?.let {
            onElementSelected.handle(ElementSelectedEvent(it.element, it.node))
        }
    }

    fun moveSelectionDown() {
        selection?.down(elements, this.children)?.let {
            selection = it
        }
    }

    fun moveSelectionUp() {
        selection?.up(elements, this.children)?.let {
            selection = it
        }
    }

    fun addElement(element: E, node: Node) {
        if (children.isEmpty()) {
            this.selection = Selection(element, node)
        }
        this.elements.add(element)
        this.children.add(node)
    }

    fun clear() {
        this.elements.clear()
        this.children.clear()
    }

    class ElementSelectedEvent<E>(
        val element: E,
        val node: Node
    ): Event(NORMAL) {

        companion object {
            val NORMAL: EventType<ElementSelectedEvent<*>> = EventType("normal")
        }

    }

    private class Selection<E>(
        val element: E, val node: Node, private val index: Int = 0
    ) {
        /**
         * @return a new selection one down, or `null` if this is the last selection
         */
        fun down(repositories: List<E>, nodes: List<Node>): Selection<E>? {
            val index = index + 1
            return if (index >= repositories.size) {
                null
            } else Selection(repositories[index], nodes[index], index)
        }

        /**
         * @return a new selection one up, or `null` if this is the first selection
         */
        fun up(repositories: List<E>, nodes: List<Node>): Selection<E>? {
            val index = index - 1
            return if (index < 0) {
                null
            } else Selection(repositories[index], nodes[index], index)
        }
    }

}