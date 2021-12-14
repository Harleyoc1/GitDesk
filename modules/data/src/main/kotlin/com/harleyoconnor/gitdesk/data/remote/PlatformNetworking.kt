package com.harleyoconnor.gitdesk.data.remote

import java.net.URL

/**
 * Gets platform-specific, Git-related data from remote APIs.
 */
interface PlatformNetworking {

    fun getRemoteRepository(username: String, repository: String): RemoteRepository?

    fun getRemoteRepository(url: URL): RemoteRepository?

    fun getLicense(key: String): License?

}