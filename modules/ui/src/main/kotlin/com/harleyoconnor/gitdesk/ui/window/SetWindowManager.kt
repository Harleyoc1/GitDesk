package com.harleyoconnor.gitdesk.ui.window

import com.harleyoconnor.gitdesk.ui.menu.MenuWindow
import javafx.stage.Stage

/**
 * @author Harley O'Connor
 */
class SetWindowManager : WindowManager {

    private val windows: MutableSet<Window> = mutableSetOf()

    override fun setOpen(window: Window) {
        this.windows.add(window)
    }

    override fun setClosed(window: Window) {
        this.windows.removeIf {
            it == window
        }
        if (this.noWindowsOpen() && window !is MenuWindow) {
            MenuWindow(Stage(), this).open()
        }
    }

    override fun get(id: String): Window? {
        return this.windows.stream()
            .filter { it.id == id }
            .findFirst()
            .orElse(null)
    }

    override fun windowsOpen(): Long {
        return this.windows.size.toLong()
    }

    override fun forEach(action: (Window) -> Unit) {
        this.windows.stream().forEach(action)
    }
}