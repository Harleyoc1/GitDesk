package com.harleyoconnor.gitdesk.data.syntax

import com.harleyoconnor.gitdesk.data.Data.moshi
import com.harleyoconnor.gitdesk.data.DataLoader
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.create
import com.harleyoconnor.gitdesk.util.makeDirectories
import com.harleyoconnor.gitdesk.util.pairsToMap
import com.harleyoconnor.gitdesk.util.stream
import com.harleyoconnor.gitdesk.util.syntax.SyntaxHighlighter
import okio.buffer
import okio.source
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension

/**
 * @author Harley O'Connor
 */
class SyntaxHighlightingLoader(
    syntaxHighlightingDirectory: Path
) : DataLoader<SyntaxHighlightingLoader.Data> {

    companion object {
        /**
         * Names of built-in highlighters relative to `/syntax_highlighting/` in the resources directory. Each is a Json
         * file and as such will also need `.json` suffixed for a full path.
         */
        private val BUILT_IN_HIGHLIGHTERS: Array<String> = arrayOf("java", "java_module", "kotlin")

        fun getHighlighterStreams(): Map<String, InputStream> {
            return BUILT_IN_HIGHLIGHTERS.stream()
                .map { it to getHighlighterStream(it) }
                .pairsToMap()
        }

        private fun getHighlighterStream(name: String): InputStream {
            return this::class.java.getResourceAsStream("/syntax_highlighting/$name.json")
                ?: throw RuntimeException("Syntax highlighter \"$name\" does not exist.")
        }
    }

    private val highlightingDirectory: Directory

    init {
        val highlightingFile = syntaxHighlightingDirectory.toFile()
        if (System.getProperty("gitdesk.app.syntax_highlighting.force_built_in") == "true") {
            highlightingFile.deleteRecursively()
        }
        if (highlightingFile.exists() && !highlightingFile.isDirectory) {
            this.tryMoveFile(highlightingFile, syntaxHighlightingDirectory)
        }
        if (!highlightingFile.exists()) {
            this.highlightingDirectory = Directory(highlightingFile.makeDirectories())
            this.saveBuiltInHighlighters()
        } else {
            this.highlightingDirectory = Directory(highlightingFile)
        }
    }

    /**
     * Attempts to move file at location reserved for syntax highlighting directory.
     */
    private fun tryMoveFile(file: File, highlightingDirectory: Path) {
        val moved = file.renameTo(File(highlightingDirectory.toAbsolutePath().toString() + " File"))

        if (!moved) {
            // TODO: Error window instead of crashing application.
            throw RuntimeException(
                "Failed to move file at path \"" + file.toPath() + "\" (path reserved for " +
                        "syntax highlighting directory). Please move this file to another location manually."
            )
        }
    }

    private fun saveBuiltInHighlighters() {
        getHighlighterStreams().forEach {
            this.saveBuiltInHighlighter(it.key, it.value)
        }
    }

    private fun saveBuiltInHighlighter(name: String, inputStream: InputStream) {
        val stream = File(this.highlightingDirectory.absolutePath + File.separatorChar + "$name.json")
            .create().outputStream()
        stream.write(inputStream.readAllBytes())
        stream.close()
    }

    override fun load(): Data {
        val highlighters = mutableListOf<SyntaxHighlighter>()
        Files.walk(this.highlightingDirectory.toPath(), 1)
            .filter { it.extension == "json" }
            .forEach {
                this.load(it.toFile())?.let { highlighter ->
                    highlighters.add(highlighter)
                }
            }
        return Data(highlighters.toTypedArray())
    }

    private fun load(file: File): SyntaxHighlighter? {
        return try {
            moshi.adapter(SyntaxHighlighter::class.java).fromJson(file.source().buffer())
        } catch (e: Exception) {
            LogManager.getLogger().error("Error loading syntax highlighter file \"${file.name}\".", e)
            null
        }
    }

    override fun save(data: Data) {
        TODO("Not yet implemented")
    }

    class Data(val highlighters: Array<SyntaxHighlighter>)

}