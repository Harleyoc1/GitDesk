package com.harleyoconnor.gitdesk.util.system

/**
 * @author Harley O'Connor
 */
abstract class AbstractSystemManager : SystemManager {

    private var lastTheme: SystemManager.Theme = SystemManager.Theme.LIGHT

    override fun getLastTheme(): SystemManager.Theme {
        return lastTheme
    }

    protected fun setLastTheme(theme: SystemManager.Theme) {
        this.lastTheme = theme
    }

}