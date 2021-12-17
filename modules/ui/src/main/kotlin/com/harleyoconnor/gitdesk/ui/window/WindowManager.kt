package com.harleyoconnor.gitdesk.ui.window

/**
 * @author Harley O'Connor
 */
interface WindowManager {

    fun setOpen(window: Window)

    fun setClosed(window: Window)

    fun get(id: String): Window?

    fun windowsOpen(): Long

    fun noWindowsOpen(): Boolean = this.windowsOpen() < 1

    fun forEach(action: (Window) -> Unit)

}