package com.harleyoconnor.gitdesk.data.repository

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.data.DataLoader
import com.harleyoconnor.gitdesk.data.serialisation.util.tryReadJson
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.create
import com.harleyoconnor.gitdesk.util.toUnmodifiableSet
import com.squareup.moshi.JsonAdapter
import java.io.File

/**
 * @author Harley O'Connor
 */
class RepositoryDataLoader(
    private val repositoriesFile: File,
    private val repositoriesDirectory: Directory
) : DataLoader<RepositoryDataLoader.RepositoryData> {

    override fun load(): RepositoryData {
        val data = this.repositoriesFile.tryReadJson(Repositories.ADAPTER) ?: Repositories(setOf(), setOf())
        val all = data.all.map(this::load).toSet()
        return RepositoryData(
            all,
            all.filter { repository -> data.open.stream().anyMatch { it.id == repository.id } }.toSet()
        )
    }

    private fun load(entry: Repositories.Entry): LocalRepository {
        return this.getRepositoryFile(entry.id).tryReadJson(LocalRepository.ADAPTER)!!
    }

    override fun save(data: RepositoryData) {
        data.all.forEach(this::save)
        this.saveRepositoryList(data)
    }

    private fun save(repository: LocalRepository) {
        this.getRepositoryFile(repository.id).writeText(
            LocalRepository.ADAPTER.indent("  ").toJson(repository)
        )
    }

    private fun saveRepositoryList(data: RepositoryData) {
        this.repositoriesFile.writeText(
            Repositories.ADAPTER.indent("  ").toJson(
                Repositories(
                    data.all.stream().map { Repositories.Entry(it.directory, it.id) }.toUnmodifiableSet(),
                    data.open.stream().map { Repositories.Entry(it.directory, it.id) }.toUnmodifiableSet()
                )
            )
        )
    }

    private fun getRepositoryFile(id: String): File {
        return File(this.repositoriesDirectory.path + "${File.separatorChar}$id.json").create()
    }

    class RepositoryData(val all: Set<LocalRepository>, val open: Set<LocalRepository>)

    class Repositories(
        val all: Set<Entry>,
        val open: Set<Entry>
    ) {
        companion object {
            val ADAPTER: JsonAdapter<Repositories> by lazy { Data.moshi.adapter(Repositories::class.java) }
        }

        data class Entry(val directory: Directory, val id: String)
    }

}