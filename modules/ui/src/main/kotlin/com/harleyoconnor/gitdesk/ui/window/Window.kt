package com.harleyoconnor.gitdesk.ui.window

/**
 * @author Harley O'Connor
 */
interface Window : Area {

    val id: String

    fun open()
    fun focus()
    fun close()

    /**
     * Invoked when the Application is stopping or the window closing. All used resources should be closed/saved safely
     * here.
     */
    fun closeAndSaveResources()

}