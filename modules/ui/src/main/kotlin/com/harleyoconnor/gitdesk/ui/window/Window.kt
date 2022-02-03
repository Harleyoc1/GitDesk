package com.harleyoconnor.gitdesk.ui.window

import com.harleyoconnor.gitdesk.ui.menubar.SelectableAccess
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
interface Window : Area {

    val id: String

    val title: String

    val stage: Stage

    val selectableAccess: SelectableAccess

    fun open()
    fun focus()
    fun close()

    /**
     * Invoked when the Application is stopping or the window closing. All used resources should be closed/saved safely
     * here.
     */
    fun closeAndSaveResources()

}