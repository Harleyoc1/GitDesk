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
) : Serialiser<LocalRepository, Unit> {

    override fun serialise(data: LocalRepository) {
        getDestinationFile(data.id).writeText(adapter.toJson(data))
    }

    private fun getDestinationFile(id: String): File {
        val file = File(repositoriesDirectory.absolutePath + File.separatorChar + "$id.json")
        file.createNewFile()
        return file
    }

}