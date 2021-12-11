package com.harleyoconnor.gitdesk.data.serialisation.adapter

import com.harleyoconnor.gitdesk.data.syntax.SyntaxHighlighterType
import com.harleyoconnor.gitdesk.data.syntax.SyntaxHighlighterTypes
import com.harleyoconnor.gitdesk.util.syntax.SyntaxHighlighter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

/**
 * @author Harley O'Connor
 */
object SyntaxHighlighterAdapter : JsonAdapter<SyntaxHighlighter>() {

    override fun fromJson(reader: JsonReader): SyntaxHighlighter? {
        var highlighter: SyntaxHighlighter? = null

        reader.beginObject()
        while (reader.hasNext()) {
            highlighter = readHighlighter(reader)
        }
        reader.endObject()

        return highlighter
    }

    private fun readHighlighter(reader: JsonReader): SyntaxHighlighter? {
        findName(reader, "type")
        val type = readType(reader)
        findName(reader, "configuration")
        return type.adapter.fromJson(reader)
    }

    private fun findName(reader: JsonReader, name: String) {
        while (reader.hasNext()) {
            if (reader.peek() == JsonReader.Token.NAME && reader.nextName() == name) {
                return
            }
        }
        throw JsonDataException("Syntax highlighter did not define \"$name\" or did not define it in the right place.")
    }

    private fun readType(reader: JsonReader): SyntaxHighlighterType<*> {
        val id = reader.nextString()
        return SyntaxHighlighterTypes.get(id)
            ?: throw JsonDataException("No such syntax highlighter type \"$id\".")
    }

    override fun toJson(writer: JsonWriter, value: SyntaxHighlighter?) {
        if (value == null) {
            writer.beginObject().endObject()
            return
        }

        writer.beginObject()
        writeType(writer, value)
        writeConfiguration(writer, value)
        writer.endObject()
    }

    private fun writeType(writer: JsonWriter, value: SyntaxHighlighter) {
        writer.name("type").value(SyntaxHighlighterTypes.idFor(value.javaClass))
    }

    private fun writeConfiguration(writer: JsonWriter, value: SyntaxHighlighter) {
        writer.name("configuration")
        SyntaxHighlighterTypes.typeFor(value.javaClass)?.adapter?.toJson(writer, value) ?: run {
            writer.beginObject().endObject()
        }
    }

}