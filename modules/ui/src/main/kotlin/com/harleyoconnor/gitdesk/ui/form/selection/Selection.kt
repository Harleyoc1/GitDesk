package com.harleyoconnor.gitdesk.ui.form.selection

/**
 * An object to be used as a [javafx.scene.control.ChoiceBox] item. Holds the underlying selection item, of type T, or
 * nothing if this is a "dummy" selection.
 */
interface Selection<T> {
    /**
     * @return the underlying item object, or `null` if this is not an actual selection
     */
    fun get(): T? = null
}