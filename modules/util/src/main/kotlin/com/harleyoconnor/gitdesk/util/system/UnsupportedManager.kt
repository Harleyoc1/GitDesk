package com.harleyoconnor.gitdesk.util.system

import com.harleyoconnor.gitdesk.util.getOsName
import com.harleyoconnor.gitdesk.util.getUserHome
import com.harleyoconnor.gitdesk.util.process.ProceduralProcessBuilder
import com.harleyoconnor.gitdesk.util.system.SystemManager.Theme
import org.apache.logging.log4j.LogManager
import java.io.File
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
     * @return The user's home directory.
     */
    override fun getAppDataLocation() = getUserHome() + separatorChar

    override fun openInFileBrowser(file: File): ProceduralProcessBuilder {
        LogManager.getLogger()
            .warn(
                "Tried to open file {} in file browser, but {} does not support this action.",
                file,
                getOsName()
            )
        return ProceduralProcessBuilder().command("")
    }

    override fun openInApp(file: File): ProceduralProcessBuilder {
        LogManager.getLogger()
            .warn(
                "Tried to open file {} in default app, but {} does not support this action.",
                file,
                getOsName()
            )
        return ProceduralProcessBuilder().command("")
    }

    override fun openInApp(file: File, applicationFile: File): ProceduralProcessBuilder {
        LogManager.getLogger()
            .warn(
                "Tried to open file {} in app {}, but {} does not support this action.",
                file,
                applicationFile,
                getOsName()
            )
        return ProceduralProcessBuilder().command("")
    }
}