package com.harleyoconnor.gitdesk.git.repository

import com.harleyoconnor.gitdesk.git.gitCommand
import com.harleyoconnor.gitdesk.util.indexOf
import com.harleyoconnor.gitdesk.util.map
import com.harleyoconnor.gitdesk.util.process.FunctionalProcessBuilder
import com.harleyoconnor.gitdesk.util.process.ProceduralProcessBuilder
import com.harleyoconnor.gitdesk.util.process.logFailure

/**
 * @author Harley O'Connor
 */
data class Branch(
    private val repository: Repository,
    val name: String
) {

    fun rename(name: String): FunctionalProcessBuilder<Branch> {
        return FunctionalProcessBuilder {
            Branch(repository, name)
        }
            .gitCommand()
            .arguments("branch", "-m", this.name, name)
            .directory(repository.directory)
    }

    fun isCheckedOut(): Boolean {
        return repository.getCurrentBranch().name == name
    }

    fun checkOut(): ProceduralProcessBuilder {
        return if (isRemoteBranch()) {
            val localBranchName = name.substring(name.indexOf(2, '/') + 1)
            repository.fetchRemoteBranch(asRemoteBranch(), localBranchName)
                .ifSuccessful {
                    Branch(repository, localBranchName).checkOut().beginAndWaitFor()
                }
                .ifFailure(::logFailure)
        } else {
            ProceduralProcessBuilder()
                .gitCommand()
                .arguments("checkout", name)
        }.directory(repository.directory)
    }

    fun getUpstream(): RemoteBranch? {
        val remote = RemoteReference.getForBranch(name, repository.directory) ?: return null
        val remoteBranchName = FunctionalProcessBuilder.normal()
            .gitCommand()
            .arguments("config", "--get", "branch.$name.merge")
            .directory(repository.directory)
            .beginAndWaitFor().result
            ?.map { it.substringAfter("refs/heads/") }
        return remoteBranchName?.let { RemoteBranch(remote, it) }
    }

    fun setUpstream(upstream: RemoteBranch): ProceduralProcessBuilder {
        return ProceduralProcessBuilder()
            .gitCommand()
            .arguments("branch", "-u", upstream.remote.name + "/" + upstream.name)
            .directory(repository.directory)
    }

    fun delete(): ProceduralProcessBuilder {
        return ProceduralProcessBuilder()
            .gitCommand()
            .arguments("branch", "-d", name)
            .directory(repository.directory)
    }

    fun isRemoteBranch(): Boolean {
        return name.startsWith("remotes/")
    }

    fun asRemoteBranch(): RemoteBranch {
        assert(this.isRemoteBranch()) {
            "Tried to get local branch as RemoteBranch."
        }
        val remoteName = name.substring(name.indexOf('/') + 1, name.indexOf(2, '/'))
        val remoteBranchName = name.substring(name.indexOf(2, '/') + 1)
        return RemoteBranch(
            RemoteReference(remoteName, Remote.getRemote(Remote.getUrl(repository.directory, remoteName)!!)),
            remoteBranchName
        )
    }

}