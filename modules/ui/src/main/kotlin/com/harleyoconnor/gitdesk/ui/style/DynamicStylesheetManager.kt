package com.harleyoconnor.gitdesk.ui.style

import com.harleyoconnor.gitdesk.ui.style.theme.Themed
import com.harleyoconnor.gitdesk.ui.style.theme.ThemedManager
import com.harleyoconnor.gitdesk.util.addIfAbsent
import com.harleyoconnor.gitdesk.util.system.SystemManager
import javafx.scene.Scene

/**
 *
 * @author Harley O'Connor
 */
class DynamicStylesheetManager(private val scene: Scene) : StylesheetManager, Themed {

    private val sheets: MutableSet<Stylesheet> = mutableSetOf()

    init {
        ThemedManager.register(this)
    }

    override fun registerSheets(vararg stylesheets: Stylesheet) {
        this.sheets.addAll(stylesheets)
        this.update()
    }

    override fun unregisterSheets(vararg stylesheets: Stylesheet) {
        this.sheets.removeAll(stylesheets)
        this.update()
    }

    override fun unregisterAllSheets() {
        this.sheets.clear()
        this.update()
    }

    override fun compareTo(other: Themed): Int {
        return Integer.MAX_VALUE
    }

    override fun update(previous: SystemManager.Theme, new: SystemManager.Theme) {
        this.update()
    }

    private fun update() {
        this.removeUncontrolledSheets()
        this.addAbsentSheets()
    }

    private fun removeUncontrolledSheets() {
        this.scene.stylesheets.removeIf { sheet ->
            this.sheets.stream().noneMatch { stylesheet ->
                stylesheet.path == sheet
            }
        }
    }

    private fun addAbsentSheets() {
        this.sheets.stream()
            .map { stylesheet -> stylesheet.path }
            .forEach { sheetPath -> this.scene.stylesheets.addIfAbsent(sheetPath) }
    }

}