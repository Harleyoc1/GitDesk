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
    override fun deserialise(key: String): LocalRepository {
        return adapter.fromJson(getDestinationFile(key).readText())
            ?: throw JsonDataException("Cannot deserialise repository with id `$key`: invalid Json.")
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