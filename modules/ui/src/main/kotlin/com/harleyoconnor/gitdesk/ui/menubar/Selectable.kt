package com.harleyoconnor.gitdesk.ui.menubar

import javafx.scene.Node
import javafx.scene.control.TextField
import org.fxmisc.richtext.GenericStyledArea

/**
 *
 * @author Harley O'Connor
 */
interface Selectable {

    /**
     * @return `true` if this selectable is currently in focus
     */
    fun isFocused(): Boolean

    /**
     * @return the selected text
     */
    fun getSelection(): String

    /**
     * Removes the selected text.
     *
     * @return the text that was removed
     */
    fun removeSelection(): String

    /**
     * Replaces the selected text with the specified [text].
     */
    fun replaceSelection(text: String)

    companion object {
        fun forNode(node: Node): Selectable? {
            return when {
                (node is TextField) -> SelectableTextField(node)
                (node is GenericStyledArea<*, *, *>) -> SelectableGenericStyledArea(node)
                else -> null
            }
        }
    }

}