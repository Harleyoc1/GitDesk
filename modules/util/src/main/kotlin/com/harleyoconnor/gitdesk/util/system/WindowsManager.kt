package com.harleyoconnor.gitdesk.util.system

import com.harleyoconnor.gitdesk.util.getUserHome

/**
 * A [SystemManager] for Windows 10.
 *
 * @author Harley O'Connor
 */
class WindowsManager : AbstractSystemManager() {

    companion object {
        const val NAME = "Windows 10"
    }

    override fun getSystemName(): String {
        return NAME
    }

    override fun getTheme(): SystemManager.Theme {
        // TODO: Find out how to get Windows 10 system theme.
        return SystemManager.Theme.LIGHT
    }

    override fun getAppDataLocation() = getUserHome() + "/AppData/Roaming/"

}