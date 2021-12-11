package com.harleyoconnor.gitdesk.util.system

import com.harleyoconnor.gitdesk.util.getUserHome
import com.harleyoconnor.gitdesk.util.system.SystemManager.Theme
import java.io.File.separatorChar

/**
 * An implementation of [SystemManager] for use when the current operating
 * system was not recognised or is unsupported.
 *
 * @author Harley O'Connor
 */
class UnsupportedManager : SystemManager {

    companion object {
        const val UNSUPPORTED = "Unsupported"
    }

    /**
     * @return [UNSUPPORTED]
     */
    override fun getSystemName(): String {
        return UNSUPPORTED
    }

    /**
     * @return [Theme.LIGHT]
     */
    override fun getTheme(): Theme {
        return Theme.LIGHT
    }

    /**
     * @return [Theme.LIGHT]
     */
    override fun getLastTheme(): Theme {
        return Theme.LIGHT
    }

    /**
     * @return The user's home directory.
     */
    override fun getAppDataLocation() = getUserHome() + separatorChar

}