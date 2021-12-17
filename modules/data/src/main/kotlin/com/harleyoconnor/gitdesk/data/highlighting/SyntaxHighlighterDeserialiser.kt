package com.harleyoconnor.gitdesk.data.highlighting

import com.harleyoconnor.gitdesk.data.serialisation.Deserialiser
import com.harleyoconnor.gitdesk.data.serialisation.Typed
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.syntax.SyntaxHighlighter
import com.squareup.moshi.JsonDataException
import java.io.File

/**
 *
 * @author Harley O'Connor
 */
class SyntaxHighlighterDeserialiser(
    private val syntaxHighlightingDirectory: Directory
): Deserialiser<String, SyntaxHighlighter> {

    @Throws(JsonDataException::class)
    override fun deserialise(key: String): SyntaxHighlighter {
        val json = getDestinationFile(key).readText()
        val type = Typed.ADAPTER.fromJson(json)?.type
            ?: throw JsonDataException("Cannot deserialise syntax highlighter with key `$key`: type not defined.")
        return SyntaxHighlighterTypes.get(type)!!.adapter.fromJson(json)
            ?: throw JsonDataException("Cannot deserialise syntax highlighter with key `$key`: invalid Json.")
    }

    @Throws(JsonDataException::class)
    private fun getDestinationFile(key: String): File {
        val file = File(syntaxHighlightingDirectory.absolutePath + File.separatorChar + "$key.json")
        assert(file.exists()) {
            JsonDataException("Cannot deserialise syntax highlighter with key `$key`: file did not exist.")
        }
        return file
    }


}