package com.harleyoconnor.gitdesk.ui.menubar

import javafx.scene.input.Clipboard.getSystemClipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DataFormat

/**
 *
 * @author Harley O'Connor
 */
class EditMenuBehaviour(
    private val selectableAccess: SelectableAccess
) {

    fun cut() {
        selectableAccess.getCurrent()?.let { selectable ->
            val selection = selectable.removeSelection()
            if (selection.isNotEmpty()) {
                copyToClipboard(selection)
            }
        }
    }

    fun copy() {
        selectableAccess.getCurrent()?.let { selectable ->
            copyToClipboard(selectable.getSelection())
        }
    }

    private fun copyToClipboard(text: String) {
        getSystemClipboard().setContent(
            ClipboardContent().also { it.putString(text) }
        )
    }

    fun paste() {
        selectableAccess.getCurrent()?.let { selectable ->
            if (getSystemClipboard().hasContent(DataFormat.PLAIN_TEXT)) {
                selectable.replaceSelection(getSystemClipboard().string)
            }
        }
    }

}