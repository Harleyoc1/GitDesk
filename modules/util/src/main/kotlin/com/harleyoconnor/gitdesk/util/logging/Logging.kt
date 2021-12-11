package com.harleyoconnor.gitdesk.util.logging

import com.harleyoconnor.gitdesk.util.system.SystemManager
import java.io.File.separatorChar

fun setLoggingDirectoryProperty() {
    System.setProperty(
        "logging.directory",
        SystemManager.get().getAppDataLocation() + separatorChar + "GitDesk" + separatorChar + "Logs"
    )
}