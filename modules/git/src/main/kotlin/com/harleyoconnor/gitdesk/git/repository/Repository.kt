package com.harleyoconnor.gitdesk.git.repository

import com.harleyoconnor.gitdesk.git.gitCommand
import com.harleyoconnor.gitdesk.git.repositoryExistsAt
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.process.FunctionalProcessBuilder
import com.harleyoconnor.gitdesk.util.process.ProceduralProcessBuilder

/**
 * @author Harley O'Connor
 */
class Repository @Throws(NoSuchRepositoryException::class) constructor(val directory: Directory) {

    val name: String by lazy { this.directory.name }

    init {
        this.throwIfDoesNotExist()
    }

    fun getCurrentBranch(): Branch {
        return Branch(this, this.getCurrentBranchName())
    }

    fun fetch(): ProceduralProcessBuilder {
        return ProceduralProcessBuilder()
            .gitCommand()
            .arguments("fetch")
            .directory(directory)
    }

    private fun getCurrentBranchName(): String {
        return FunctionalProcessBuilder.normal()
            .gitCommand()
            .arguments("branch", "--show-current")
            .directory(directory)
            .beginAndWaitFor().result!!
    }

    @Throws(NoSuchRepositoryException::class)
    private fun throwIfDoesNotExist() {
        if (!repositoryExistsAt(directory)) {
            throw NoSuchRepositoryException("Git repository does not exist at \"${directory.path}\".")
        }
    }

}