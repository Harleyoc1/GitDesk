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

    fun getRemoteRepository(): RemoteRepository? {
        return platform.networking?.getRemoteRepository(url)
    }

}