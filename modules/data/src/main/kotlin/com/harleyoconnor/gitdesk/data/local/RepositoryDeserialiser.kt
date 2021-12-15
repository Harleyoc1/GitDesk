package com.harleyoconnor.gitdesk.data.local

import com.harleyoconnor.gitdesk.data.serialisation.Deserialiser
import com.harleyoconnor.gitdesk.util.Directory
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import java.io.File

/**
 * @author Harley O'Connor
 */
class RepositoryDeserialiser(
    private val adapter: JsonAdapter<LocalRepository>,
    private val repositoriesDirectory: Directory
) : Deserialiser<String, LocalRepository> {

    @Throws(JsonDataException::class)
    override fun deserialise(input: String): LocalRepository {
        return adapter.fromJson(getDestinationFile(input).readText())
            ?: throw JsonDataException("Cannot deserialise repository with id `$input`: invalid Json.")
    }

    @Throws(JsonDataException::class)
    private fun getDestinationFile(id: String): File {
        val file = File(repositoriesDirectory.absolutePath + File.separatorChar + "$id.json")
        assert(file.exists()) {
            JsonDataException("Cannot deserialise repository with id `$id`: file did not exist.")
        }
        return file
    }

}