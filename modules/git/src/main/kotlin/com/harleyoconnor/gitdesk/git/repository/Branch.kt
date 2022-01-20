package com.harleyoconnor.gitdesk.git.repository

import com.harleyoconnor.gitdesk.git.gitCommand
import com.harleyoconnor.gitdesk.util.map
import com.harleyoconnor.gitdesk.util.process.FunctionalProcessBuilder
import com.harleyoconnor.gitdesk.util.process.ProceduralProcessBuilder

/**
 * @author Harley O'Connor
 */
data class Branch(
    private val repository: Repository,
    val name: String,
    val checkedOut: Boolean,
    val upstream: Upstream?
) {

    class Upstream(
        val remote: RemoteReference,
        val branch: String?
    )

    fun checkOut(): ProceduralProcessBuilder {
        val builder = ProceduralProcessBuilder()
            .gitCommand()
            .arguments("checkout")
        if (name.startsWith("remotes/")) {
            builder.arguments("--track").arguments(name.substringAfter("remotes/"))
        } else {
            builder.arguments(name)
        }
        return builder.directory(repository.directory)
    }

    fun getUpstream(): RemoteReference? {
        return Remote.getUpstreamName(repository.directory, name)?.map { remoteName ->
            Remote.getUrl(repository.directory, remoteName)?.map { remoteUrl ->
                RemoteReference(name, Remote.getRemote(remoteUrl))
            }
        }
    }

}