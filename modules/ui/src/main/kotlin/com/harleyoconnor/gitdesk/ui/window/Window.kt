package com.harleyoconnor.gitdesk.ui.window

/**
 * @author Harley O'Connor
 */
interface Window : Area {

    val id: String

    fun open()
    fun focus()
    fun close()

}