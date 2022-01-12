package com.harleyoconnor.gitdesk.git.repository

import com.harleyoconnor.gitdesk.util.map

/**
 * @author Harley O'Connor
 */
data class Branch(
    private val repository: Repository,
    val name: String,
    val checkedOut: Boolean
) {

    fun getUpstream(): RemoteReference? {
        return Remote.getUpstreamName(repository.directory, name)?.map { remoteName ->
            Remote.getUrl(repository.directory, remoteName)?.map { remoteUrl ->
                RemoteReference(name, Remote.getRemote(remoteUrl))
            }
        }
    }

}