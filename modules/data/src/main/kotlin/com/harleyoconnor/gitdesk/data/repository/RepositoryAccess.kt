package com.harleyoconnor.gitdesk.data.repository

import com.harleyoconnor.gitdesk.data.DataManager
import com.harleyoconnor.gitdesk.git.repository.Repository

/**
 * @author Harley O'Connor
 */
interface RepositoryAccess : DataManager {

    fun add(repository: Repository)

    fun open(repository: Repository)

    fun close(repository: Repository)

    fun get(repository: Repository): LocalRepository?

    fun getOpen(repository: Repository): LocalRepository?

    fun getClosed(repository: Repository): LocalRepository?

    fun forEach(action: (LocalRepository) -> Unit)

    fun forEachOpen(action: (LocalRepository) -> Unit)

    fun forEachClosed(action: (LocalRepository) -> Unit)

}