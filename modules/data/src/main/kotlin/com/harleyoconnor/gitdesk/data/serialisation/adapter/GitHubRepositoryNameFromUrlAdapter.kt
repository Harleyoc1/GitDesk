package com.harleyoconnor.gitdesk.data.serialisation.adapter

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.github.GitHubNetworking
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.util.regex.Pattern

/**
 * @author Harley O'Connor
 */
object GitHubRepositoryNameFromUrlAdapter : JsonAdapter<RemoteRepository.Name>() {

    val urlPattern = Pattern.compile("https://api\\.github\\.com/repos" +
            "/([${GitHubNetworking.acceptableUsernameRange}]{4,})" +
            "/([${GitHubNetworking.acceptableRepositoryRange}]+)")

    override fun fromJson(reader: JsonReader): RemoteRepository.Name? {
        return if (reader.peek() == JsonReader.Token.NULL) {
            null
        } else if (reader.peek() == JsonReader.Token.STRING) {
            fromUrl(reader.nextString())
        } else {
            throw JsonDataException("Unsupported Json token for repository URL: ${reader.peek()}")
        }
    }

    private fun fromUrl(url: String): RemoteRepository.Name? {
        val matcher = urlPattern.matcher(url)
        return if (matcher.find()) {
            RemoteRepository.Name(matcher.group(1), matcher.group(2))
        } else null
    }

    override fun toJson(writer: JsonWriter, value: RemoteRepository.Name?) {
        if (value == null) {
            writer.nullValue()
        } else {
            writer.value("https://api.github.com/repos/${value.getFullName()}")
        }
    }
}