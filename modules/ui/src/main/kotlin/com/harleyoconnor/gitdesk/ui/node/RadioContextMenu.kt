package com.harleyoconnor.gitdesk.ui.node

import javafx.scene.control.CheckMenuItem
import javafx.scene.control.ContextMenu

/**
 * A context menu in which one child item, representing an option, can be selected/checked.
 *
 * @author Harley O'Connor
 */
class RadioContextMenu<E>(
    options: Map<String, E>,
    default: E,
    private val selectionChangedCallback: (E) -> Unit
) : ContextMenu() {

    private var selected: CheckMenuItem? = null

    init {
        options.forEach { (name, element) ->
            if (element == default) {
                addDefaultOption(name, element)
            } else {
                addOption(name, element)
            }
        }
    }

    private fun addOption(name: String, element: E) {
        items.add(
            CheckMenuItem(name).also { item ->
                item.setOnAction {
                    itemSelected(item, element)
                }
            }
        )
    }


    private fun addDefaultOption(name: String, element: E) {
        items.add(
            CheckMenuItem(name).also { item ->
                item.isSelected = true
                selected = item
                item.setOnAction {
                    itemSelected(item, element)
                }
            }
        )
    }

    private fun itemSelected(item: CheckMenuItem, element: E) {
        if (selected == item) {
            item.isSelected = true
            return
        }
        selected?.let {
            it.isSelected = false
        }
        selected = item
        selectionChangedCallback(element)
    }

}