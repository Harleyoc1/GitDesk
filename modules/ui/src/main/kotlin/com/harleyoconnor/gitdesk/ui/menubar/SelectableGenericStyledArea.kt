package com.harleyoconnor.gitdesk.ui.menubar

import org.fxmisc.richtext.GenericStyledArea

/**
 *
 * @author Harley O'Connor
 */
data class SelectableGenericStyledArea(
    private val styledArea: GenericStyledArea<*, *, *>
) : Selectable {

    override fun isFocused(): Boolean = styledArea.isFocused

    override fun getSelection(): String = styledArea.selectedText

    override fun removeSelection(): String {
        val selection = getSelection()
        styledArea.deleteText(styledArea.selection)
        return selection
    }

    override fun replaceSelection(text: String) = styledArea.replaceSelection(text)
}