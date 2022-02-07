package com.harleyoconnor.gitdesk.util.system

import com.harleyoconnor.gitdesk.util.getUserHome
import com.harleyoconnor.gitdesk.util.process.ProceduralProcessBuilder
import java.io.File

/**
 * A [SystemManager] for Windows 10.
 *
 * @author Harley O'Connor
 */
class WindowsManager : SystemManager {

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

    override fun openInFileBrowser(file: File): ProceduralProcessBuilder =
        ProceduralProcessBuilder().command("explorer").arguments("/select, ${file.path}")

    override fun openInApp(file: File): ProceduralProcessBuilder =
        ProceduralProcessBuilder().command("\"${file.canonicalPath}\"")

    override fun openInApp(file: File, applicationFile: File): ProceduralProcessBuilder =
        ProceduralProcessBuilder().command("\"${applicationFile.canonicalPath}\" \"${file.canonicalPath}\"")

}