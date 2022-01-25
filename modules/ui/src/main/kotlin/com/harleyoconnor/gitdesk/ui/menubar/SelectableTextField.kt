package com.harleyoconnor.gitdesk.ui.menubar

import javafx.scene.control.TextField

/**
 *
 * @author Harley O'Connor
 */
data class SelectableTextField(
    private val textField: TextField
) : Selectable {

    override fun isFocused(): Boolean = textField.isFocused

    override fun getSelection(): String = textField.selectedText

    override fun removeSelection(): String {
        val selection = getSelection()
        textField.deleteText(textField.selection)
        return selection
    }

    override fun replaceSelection(text: String) = textField.replaceSelection(text)
}