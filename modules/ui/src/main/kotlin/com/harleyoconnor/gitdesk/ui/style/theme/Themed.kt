package com.harleyoconnor.gitdesk.ui.style.theme

import com.harleyoconnor.gitdesk.util.system.SystemManager

/**
 * Represents something that changes depending on the current [SystemManager.Theme].
 *
 * @author Harley O'Connor
 */
interface Themed : Comparable<Themed> {

    /**
     * Called when the [SystemManager.Theme] changes.
     *
     * @param previous the previously active [SystemManager.Theme]
     * @param new the newly active [SystemManager.Theme]
     */
    fun update(previous: SystemManager.Theme, new: SystemManager.Theme)

}