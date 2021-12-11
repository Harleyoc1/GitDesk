package com.harleyoconnor.gitdesk.ui.window

/**
 *
 * @author Harley O'Connor
 */
interface FixedArea : Area {

    val width: Double
    val height: Double

    override val minWidth: Double get() = width
    override val minHeight: Double get() = height

    override val maxWidth: Double get() = width
    override val maxHeight: Double get() = height

    override val resizable: Boolean get() = false
}