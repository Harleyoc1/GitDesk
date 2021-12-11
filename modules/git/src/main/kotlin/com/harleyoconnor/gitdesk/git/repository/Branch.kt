package com.harleyoconnor.gitdesk.git.repository

import com.harleyoconnor.gitdesk.git.gitCommand
import com.harleyoconnor.gitdesk.util.process.FunctionalProcessBuilder

/**
 * @author Harley O'Connor
 */
class Branch(
    private val repository: Repository,
    val name: String
) {

    fun getUpstream(): Remote {
        return Remote(this.repository, this.getRemoteName())
    }

    private fun getRemoteName(): String {
        return FunctionalProcessBuilder.normal()
            .gitCommand()
            .arguments("config", "--get", "branch.$name.remote")
            .directory(repository.directory)
            .beginAndWaitFor().result!!
    }

}