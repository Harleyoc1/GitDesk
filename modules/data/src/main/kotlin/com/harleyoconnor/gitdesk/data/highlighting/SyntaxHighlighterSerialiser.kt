package com.harleyoconnor.gitdesk.data.highlighting

import com.harleyoconnor.gitdesk.data.serialisation.Serialiser
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.syntax.SyntaxHighlighter
import java.io.File

/**
 *
 * @author Harley O'Connor
 */
class SyntaxHighlighterSerialiser(
    private val syntaxHighlightingDirectory: Directory
): Serialiser<String, SyntaxHighlighter> {

    override fun serialise(key: String, data: SyntaxHighlighter) {
        val type = SyntaxHighlighterTypes.getFor(data)!!
        getDestinationFile(key).writeText(
            SyntaxHighlighterTypes.getFor(data)!!.adapter.toJson(data)
                .replaceFirst("{", "{\n  \"type\": \"$type\"")
        )
    }

    private fun getDestinationFile(key: String): File {
        val file = File(syntaxHighlightingDirectory.absolutePath + File.separatorChar + "$key.json")
        file.createNewFile()
        return file
    }

}