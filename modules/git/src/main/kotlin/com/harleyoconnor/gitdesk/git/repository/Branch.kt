package com.harleyoconnor.gitdesk.git.repository

/**
 * @author Harley O'Connor
 */
class Branch(
    private val repository: Repository,
    val name: String
) {

    fun getUpstream(): RemoteReference {
        val name = Remote.getUpstreamName(repository.directory, name)
        val url = Remote.getUrl(repository.directory, name)
        return RemoteReference(name, Remote.getRemote(url))
    }

}