package com.harleyoconnor.gitdesk.git

import java.net.URL

/**
 * @author Harley O'Connor
 */
class Credentials(
    private val username: String,
    private val accessKey: String
) {

    fun addToUrl(url: URL): URL {
        return URL(url.toExternalForm().replace("://", "://${username}:${accessKey}@"))
    }

}