package com.harleyoconnor.gitdesk.data.local

import com.harleyoconnor.gitdesk.data.Data.moshi
import com.harleyoconnor.gitdesk.git.repository.Repository
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.map
import com.squareup.moshi.JsonAdapter
import java.io.File

/**
 * @author Harley O'Connor
 */
class RepositoryHolder(
    repositoriesFile: File,
    repositoriesDirectory: Directory
) : RepositoryAccess {

    private val all: MutableSet<LocalRepository> = mutableSetOf()
    private val open: MutableSet<LocalRepository> = mutableSetOf()
    private val loader: RepositoryDataLoader = RepositoryDataLoader(repositoriesFile, repositoriesDirectory)

    override fun load() {
        val data = this.loader.load()

        this.all.clear()
        this.all.addAll(data.all)

        this.open.clear()
        this.open.addAll(data.open)
    }

    override fun save() {
        this.loader.save(RepositoryDataLoader.RepositoryData(this.all, this.open))
    }

    override fun add(repository: Repository) {
        this.all.add(LocalRepository(this.createId(repository), repository.directory))
    }

    private fun createId(repository: Repository): String {
        val name = repository.name
        val nameLength = name.length
        return this.all.stream()
            .filter { it.id.startsWith(name) && it.id.length > nameLength }
            .mapToInt {
                Integer.parseInt(it.id.substring(nameLength))
            }
            .max()
            .map { name + it }
            .orElse(name)
    }

    override fun open(repository: Repository) {
        this.get(repository)?.let(this.open::add)
    }

    override fun close(repository: Repository) {
        this.get(repository)?.let {
            it.close()
            this.open.remove(it)
            this.addToOpenRecent(it)
        }
    }

    private fun addToOpenRecent(repository: LocalRepository) {
//        AppWindowManager.forEach { window ->
//            AppMenuBar.Repository.addRepositoryMenuItem(window, repository, window.menuBar.getItem(OPEN_RECENT)?.node!!)
//        }
    }

    override fun get(repository: Repository): LocalRepository? {
        return this.all.stream()
            .filter { it.directory == repository.directory }
            .findAny()
            .orElse(null)
    }

    override fun getOpen(repository: Repository): LocalRepository? {
        return this.open.stream()
            .filter { it.directory == repository.directory }
            .findAny()
            .orElse(null)
    }

    override fun getClosed(repository: Repository): LocalRepository? {
        return this.all.stream()
            .filter { it.directory == repository.directory && !this.open.contains(it) }
            .findAny()
            .orElse(null)
    }

    override fun forEach(action: (LocalRepository) -> Unit) {
        this.all.forEach(action)
    }

    override fun forEachOpen(action: (LocalRepository) -> Unit) {
        this.open.forEach(action)
    }

    override fun forEachClosed(action: (LocalRepository) -> Unit) {
        this.all.stream()
            .filter { !this.open.contains(it) }
            .forEach(action)
    }

    private data class Data(
        val all: Set<Entry> = setOf(),
        val open: Set<Entry> = setOf()
    ) {
        companion object {
            val ADAPTER: JsonAdapter<Data> by lazy { moshi.adapter(Data::class.java) }
        }

        data class Entry(private val directory: Directory, private val id: String)
    }

}