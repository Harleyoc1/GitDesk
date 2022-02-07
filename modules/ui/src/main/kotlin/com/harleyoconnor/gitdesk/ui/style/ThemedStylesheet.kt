package com.harleyoconnor.gitdesk.ui.style

import com.harleyoconnor.gitdesk.ui.style.theme.Themed
import com.harleyoconnor.gitdesk.ui.style.theme.ThemedManager
import com.harleyoconnor.gitdesk.util.system.SystemManager

/**
 *
 * @author Harley O'Connor
 */
data class ThemedStylesheet(
    private val lightThemePath: String,
    private val darkThemePath: String
) : Stylesheet, Themed {

    companion object {

        /**
         * @param basePath the base path, not including the `.css` extension
         */
        fun fromBasePath(basePath: String): Stylesheet {
            return ThemedStylesheet("$basePath-light.css", "$basePath-dark.css")
        }

    }

    override var path: String = ""

    init {
        ThemedManager.register(this)
        this.path = this.pathForTheme(ThemedManager.getLastTheme())
    }

    override fun update(previous: SystemManager.Theme, new: SystemManager.Theme) {
        this.path = this.pathForTheme(new)
    }

    private fun pathForTheme(theme: SystemManager.Theme): String = when (theme) {
        SystemManager.Theme.LIGHT -> this.lightThemePath
        SystemManager.Theme.DARK -> this.darkThemePath
    }

    override fun compareTo(other: Themed): Int {
        return if (other is ThemedStylesheet) 0 else -1
    }

}