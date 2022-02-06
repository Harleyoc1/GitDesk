package com.harleyoconnor.gitdesk.util.system

import com.harleyoconnor.gitdesk.util.getOsName
import com.harleyoconnor.gitdesk.util.process.ProceduralProcessBuilder
import com.harleyoconnor.gitdesk.util.system.SystemManager.Companion.SYSTEM_MANAGER
import com.harleyoconnor.gitdesk.util.system.SystemManager.Companion.get
import java.io.File

/**
 * A [SystemManager] handles operations that are specific to the operating system
 * the program is currently running on, such as getting the current theme
 * (which can be done via [getTheme()]).
 *
 * The main [SystemManager] instance for the current system is stored in
 * [SYSTEM_MANAGER], which can be obtained via [get].
 *
 * @author Harley O'Connor
 */
interface SystemManager {

    companion object {
        private val SYSTEM_MANAGER: SystemManager = getForOS()

        /**
         * Gets the [SystemManager] for the detected system.
         */
        fun get(): SystemManager =
            SYSTEM_MANAGER

        private fun getForOS(): SystemManager =
            when (getOsName()) {
                MacOSManager.NAME -> MacOSManager()
                WindowsManager.NAME -> WindowsManager()
                else -> UnsupportedManager()
            }
    }

    /**
     * Gets the operating system's name. This should match the result of a call to
     * [System.getProperty] with `os.name`.
     *
     * @return The name for the operating system.
     */
    fun getSystemName(): String

    /**
     * An enum stating the current theme/appearance set in the operating system.
     */
    enum class Theme {
        LIGHT, DARK
    }

    /**
     * Gets the current [Theme] for the operating system.
     *
     * Implementations should set a `lastTheme` in this method, which can
     * be queried from [getLastTheme].
     *
     * @return The current [Theme], or [Theme.LIGHT] as a default if
     *         it could not be found.
     */
    fun getTheme(): Theme

    /**
     * Gets the [Theme] last recorded from a [getTheme] query.
     *
     * @return The last recorded [Theme].
     */
    fun getLastTheme(): Theme

    /**
     * Gets the location at which application-specific data should be stored. This
     * should always end with a `/`, defining the folder location.
     */
    fun getAppDataLocation(): String

    /**
     * @return A process that opens the given [file] in the default file browser.
     */
    fun openInFileBrowser(file: File): ProceduralProcessBuilder

    /**
     * @return A process that opens the given [file] in the default application.
     */
    fun openInApp(file: File): ProceduralProcessBuilder

    /**
     * @return A process that opens the given [file] in the specified [applicationFile].
     */
    fun openInApp(file: File, applicationFile: File): ProceduralProcessBuilder

}