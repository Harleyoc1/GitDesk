package com.harleyoconnor.gitdesk.util.system

import com.harleyoconnor.gitdesk.util.getUserHome
import com.harleyoconnor.gitdesk.util.process.ProceduralProcessBuilder
import com.harleyoconnor.gitdesk.util.system.SystemManager.Theme
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.File.separatorChar
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * An implementation of [SystemManager] for MacOS.
 *
 * @author Harley O'Connor
 */
class MacOSManager : AbstractSystemManager() {

    companion object {
        const val NAME = "Mac OS X"
        private val LOGGER = LogManager.getLogger()
    }

    /**
     * Gets the operating system's name. This should match the result of a call to
     * [System.getProperty] with `os.name`.
     *
     * @return [NAME].
     */
    override fun getSystemName(): String {
        return NAME
    }

    /**
     * Updates [lastTheme] and returns the given [Theme] for
     * in-line calls.
     *
     * @param currentTheme The current [Theme].
     * @return The `currentTheme` given.
     */
    private fun updateLastTheme(currentTheme: Theme): Theme {
        return currentTheme.also { this.setLastTheme(it) }
    }

    /**
     * Gets the current [Theme] for the operating system.
     *
     * Implementations should set a `lastTheme` in this method, which can
     * be queried from [getLastTheme].
     *
     * @return The current [Theme], or [Theme.LIGHT] as a default if
     * it could not be found.
     */
    override fun getTheme(): Theme {
        return try {
            if (isDarkMode()) Theme.DARK
            else Theme.LIGHT
        } catch (e: IOException) {
            // If there was an error for whatever reason, log it and default to whatever the last detected theme was.
            this.logException(e)
            this.getLastTheme()
        } catch (e: InterruptedException) {
            this.logException(e)
            this.getLastTheme()
        } catch (e: IllegalThreadStateException) {
            // The operation timed out, so just default to whatever the last detected theme was.
            this.getLastTheme()
        }
    }

    private fun isDarkMode() = ProceduralProcessBuilder()
        .command("defaults").arguments("read", "-g", "AppleInterfaceStyle")
        .beginAndWaitFor(1, TimeUnit.SECONDS).success()

    private fun logException(e: Exception) {
        LOGGER.error(
            "Exception whilst attempting to obtain system theme; defaulting to last detected theme.",
            SystemThemeDetectionException("Unable to obtain system theme.", e)
        )
    }

    override fun getAppDataLocation() = getUserHome() + "${separatorChar}Library${separatorChar}Application Support"

    override fun openInFileBrowser(file: File): ProceduralProcessBuilder =
        ProceduralProcessBuilder().command("open").arguments("-R", file.path)
}