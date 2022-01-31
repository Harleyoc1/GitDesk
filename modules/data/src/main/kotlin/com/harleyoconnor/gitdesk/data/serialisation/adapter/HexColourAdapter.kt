package com.harleyoconnor.gitdesk.data.serialisation.adapter

import com.harleyoconnor.gitdesk.util.toHexColourString
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

/**
 *
 * @author Harley O'Connor
 */
object HexColourAdapter : JsonAdapter<Int>() {

    override fun toJson(writer: JsonWriter, hex: Int?) {
        if (hex == null) {
            writer.nullValue()
        } else {
            writer.value(hex.toHexColourString())
        }
    }

    override fun fromJson(reader: JsonReader): Int? {
        return if (reader.peek() == JsonReader.Token.NULL) {
            null
        } else if (reader.peek() == JsonReader.Token.STRING) {
            Integer.parseInt(reader.nextString(), 16)
        } else {
            throw JsonDataException("Unsupported Json token for hex colour: ${reader.peek()}")
        }
    }

}