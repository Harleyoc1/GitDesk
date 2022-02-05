package com.harleyoconnor.gitdesk.data.remote

import com.harleyoconnor.gitdesk.git.repository.Remote
import java.net.URL

/**
 * @author Harley O'Connor
 */
class RemoteRepositoryReference(
    override val url: URL,
    val platform: Platform,
    val name: RemoteRepository.Name
) : Remote {

    private val repository by lazy {
        platform.networking?.getRemoteRepository(url)
    }

    fun getRemoteRepository(): RemoteRepository? {
        return repository
    }

}