package com.harleyoconnor.gitdesk.ui.highlighting

import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.util.create
import com.harleyoconnor.gitdesk.util.pairsToMap
import com.harleyoconnor.gitdesk.util.stream
import com.harleyoconnor.gitdesk.util.system.SystemManager
import java.io.File
import java.io.InputStream

private val directory = File(
    SystemManager.get().getAppDataLocation() + File.separatorChar + "GitDesk" + File.separatorChar + "Syntax Highlighting"
)
/**
 * Names of built-in highlighters relative to `/ui/highlighting` in the resources directory. Each is a Json
 * file and as such will also need `.json` suffixed for a full path.
 */
private val builtInHighlighters: Array<String> = arrayOf("java", "java_module", "kotlin")

fun setupHighlightingDirectory() {
    if (System.getProperty("gitdesk.ui.highlighting.force_built_in") == "true") {
        directory.deleteRecursively()
    }
    if (directory.exists()) {
        if (!directory.isDirectory) {
            tryMoveFileInReservedLocation(directory)
        }
    } else {
        directory.mkdirs()
        copyBuildInHighlighters()
    }
}

private fun tryMoveFileInReservedLocation(file: File) {
    if (!file.renameTo(File(file.absolutePath + " File"))) {
        throw RuntimeException("File in reserved location ${file.absolutePath} could not be moved. " +
                "Please move or rename this manually and restart.")
    }
}

private fun copyBuildInHighlighters() {
    getHighlighterStreams().forEach { (name, stream) ->
        copyBuiltInHighlighter(name, stream)
    }
}

private fun getHighlighterStreams(): Map<String, InputStream> {
    return builtInHighlighters.stream()
        .map { it to getHighlighterStream(it) }
        .pairsToMap()
}

private fun getHighlighterStream(name: String): InputStream {
    return Application::class.java.getResourceAsStream("/ui/highlighting/$name.json")
        ?: throw RuntimeException("Syntax highlighter \"$name\" does not exist.")
}

private fun copyBuiltInHighlighter(name: String, inputStream: InputStream) {
    val outputStream = File(directory.absolutePath + File.separatorChar + "$name.json")
        .create().outputStream()
    outputStream.write(inputStream.readAllBytes())
    inputStream.close()
    outputStream.close()
}