package com.harleyoconnor.gitdesk.data.local

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.serialisation.DataAccess
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.entriesToMap
import com.harleyoconnor.gitdesk.util.pairsToMap
import com.harleyoconnor.gitdesk.util.toSet
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import java.io.File

/**
 * @author Harley O'Connor
 */
class RepositoryAccess(
    private val repositoriesFile: File,
    repositoriesDirectory: Directory
): DataAccess<Directory, LocalRepository> {

    private val serialiser: RepositorySerialiser
    private val deserialiser: RepositoryDeserialiser

    private val repositories: Repositories = Repositories.load(repositoriesFile)

    init {
        val repositoryAdapter = MOSHI.adapter(LocalRepository::class.java)
        serialiser = RepositorySerialiser(repositoryAdapter, repositoriesDirectory)
        deserialiser = RepositoryDeserialiser(repositoryAdapter, repositoriesDirectory)
    }

    @Throws(NoSuchRepositoryException::class)
    override fun get(key: Directory): LocalRepository {
        val id = repositories.all[key]
            ?: throw NoSuchRepositoryException("Repository data for `$key` does not exist.")
        try {
            val repository = deserialiser.deserialise(id)
            injectOpenField(id, repository)
            return repository
        } catch (e: JsonDataException) {
            cleanOfEntry(key, id)
            throw NoSuchRepositoryException("Repository data for `$key` does not exist.")
        }
    }

    private fun cleanOfEntry(key: Directory, id: String) {
        repositories.all.remove(key)
        repositories.open.remove(id)
        repositories.save(repositoriesFile)
    }

    private fun injectOpenField(id: String, repository: LocalRepository) {
        if (repositories.open.contains(id)) {
            repository.open = true
        }
    }

    override fun save(key: Directory, data: LocalRepository) {
        serialiser.serialise(data.id, data)
        updateRepositoriesData(data)
        repositories.save(repositoriesFile)
    }

    private fun updateRepositoriesData(data: LocalRepository) {
        repositories.all[data.directory] = data.id
        if (data.open) {
            repositories.open.add(data.id)
        } else {
            repositories.open.remove(data.id)
        }
    }

    fun getAll(): Map<Directory, String> = repositories.all

    fun getAllNotOpen(): Map<Directory, String> = repositories.all.entries.stream()
        .filter { !repositories.open.contains(it.value) }
        .entriesToMap()

    class Repositories(
        val all: MutableMap<Directory, String> = mutableMapOf(),
        val open: MutableSet<String> = mutableSetOf()
    ) {
        companion object {
            private val ADAPTER: JsonAdapter<Repositories> by lazy { MOSHI.adapter(Repositories::class.java) }

            fun load(repositoriesFile: File): Repositories {
                return try {
                    ADAPTER.fromJson(repositoriesFile.readText()) ?: Repositories()
                } catch (e: Exception) {
                    Repositories()
                }
            }
        }

        fun save(repositoriesFile: File) {
            repositoriesFile.writeText(ADAPTER.toJson(this))
        }
    }

    class NoSuchRepositoryException(message: String) : Exception(message)

}