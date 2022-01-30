package com.harleyoconnor.gitdesk.ui.node

import com.harleyoconnor.gitdesk.ui.style.SELECTED_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.util.enableBottomClass
import com.harleyoconnor.gitdesk.ui.util.enableTopClass
import com.harleyoconnor.gitdesk.ui.util.disableBottomClass
import com.harleyoconnor.gitdesk.ui.util.disableTopClass
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
            field?.let {
                onElementFocused?.handle(ElementEvent(ElementEvent.FOCUSED, it.element, it.node))
                it.node.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, true)
            }
        }

    private var onElementFocused: EventHandler<ElementEvent<E>>? = null

    private var onElementSelected: EventHandler<ElementEvent<E>>? = null

    init {
        this.styleClass.add("selection-cell-list")
    }

    fun setOnElementFocused(handler: EventHandler<ElementEvent<E>>) {
        this.onElementFocused = handler
    }

    fun setOnElementSelected(handler: EventHandler<ElementEvent<E>>) {
        this.onElementSelected = handler
    }

    fun select() {
        selection?.let {
            onElementSelected?.handle(ElementEvent(ElementEvent.SELECTED, it.element, it.node))
        }
    }

    fun select(element: E) {
        val index = elements.indexOf(element)
        if (index > -1) {
            selection = Selection(element, children[index], index)
            select()
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
        children.firstOrNull()?.disableTopClass()
        children.lastOrNull()?.disableBottomClass()
        if (children.isEmpty()) {
            this.selection = Selection(element, node)
        }
        this.elements.add(element)
        this.children.add(node)
        children.firstOrNull()?.enableTopClass()
        children.lastOrNull()?.enableBottomClass()
    }

    fun removeElement(element: E, node: Node) {
        children.firstOrNull()?.disableTopClass()
        children.lastOrNull()?.disableBottomClass()
        if (this.selection?.element == element) {
            this.moveSelectionDown()
        }
        this.elements.remove(element)
        this.children.remove(node)
        children.firstOrNull()?.enableTopClass()
        children.lastOrNull()?.enableBottomClass()
    }

    fun clear() {
        this.children.forEach {
            it.disableTopClass()
            it.disableBottomClass()
        }
        this.elements.clear()
        this.children.clear()
    }

    class ElementEvent<E>(
        type: EventType<ElementEvent<*>>,
        val element: E,
        val node: Node
    ): Event(type) {

        companion object {
            val FOCUSED: EventType<ElementEvent<*>> = EventType("focused")
            val SELECTED: EventType<ElementEvent<*>> = EventType("selected")
        }

    }

    private class Selection<E>(
        val element: E, val node: Node, private val index: Int = 0
    ) {
        /**
         * @return a new selection one down, or `null` if this is the last selection
         */
        fun down(elements: List<E>, nodes: List<Node>): Selection<E>? {
            val index = index + 1
            return if (index >= elements.size) {
                null
            } else Selection(elements[index], nodes[index], index)
        }

        /**
         * @return a new selection one up, or `null` if this is the first selection
         */
        fun up(elements: List<E>, nodes: List<Node>): Selection<E>? {
            val index = index - 1
            return if (index < 0) {
                null
            } else Selection(elements[index], nodes[index], index)
        }
    }

}