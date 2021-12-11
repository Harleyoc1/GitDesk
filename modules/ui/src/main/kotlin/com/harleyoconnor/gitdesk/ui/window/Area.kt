package com.harleyoconnor.gitdesk.ui.window

/**
 *
 * @author Harley O'Connor
 */
interface Area {

    val minWidth: Double
    val minHeight: Double

    val maxWidth: Double get() = Double.MAX_VALUE
    val maxHeight: Double get() = Double.MAX_VALUE

    val resizable: Boolean get() = true

    fun fitsIn(other: Area): Boolean =
        this.minHeight < other.maxHeight && this.minWidth < other.maxWidth

}