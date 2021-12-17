package com.harleyoconnor.gitdesk.data.local

import com.harleyoconnor.gitdesk.data.serialisation.Serialiser
import com.harleyoconnor.gitdesk.util.Directory
import com.squareup.moshi.JsonAdapter
import java.io.File

/**
 * @author Harley O'Connor
 */
class RepositorySerialiser(
    private val adapter: JsonAdapter<LocalRepository>,
    private val repositoriesDirectory: Directory
) : Serialiser<String, LocalRepository> {

    override fun serialise(key: String, data: LocalRepository) {
        getDestinationFile(key).writeText(adapter.toJson(data))
    }

    private fun getDestinationFile(id: String): File {
        val file = File(repositoriesDirectory.absolutePath + File.separatorChar + "$id.json")
        file.createNewFile()
        return file
    }

}