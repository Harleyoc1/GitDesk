package com.harleyoconnor.gitdesk.git.repository

import com.harleyoconnor.gitdesk.git.gitCommand
import com.harleyoconnor.gitdesk.git.repositoryExistsAt
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.map
import com.harleyoconnor.gitdesk.util.process.FunctionalProcessBuilder
import com.harleyoconnor.gitdesk.util.process.ProceduralProcessBuilder
import com.harleyoconnor.gitdesk.util.process.Response
import com.harleyoconnor.gitdesk.util.substringUntil
import com.harleyoconnor.gitdesk.util.toTypedArray
import java.io.File
import java.util.regex.Pattern

/**
 * @author Harley O'Connor
 */
data class Repository @Throws(NoSuchRepositoryException::class) constructor(val directory: Directory) {

    companion object {
        val REMOTE_BRANCH_PATTERN = Pattern.compile("\\[(.*?)]")
    }

    val name: String by lazy { this.directory.name }

    init {
        this.throwIfDoesNotExist()
    }

    fun getCurrentBranch(): Branch {
        val branchName = this.getCurrentBranchName()
        return Branch(this, branchName, true, getBranchUpstream(branchName))
    }

    private fun getCurrentBranchName(): String {
        return FunctionalProcessBuilder.normal()
            .gitCommand()
            .arguments("branch", "--show-current")
            .directory(directory)
            .beginAndWaitFor().result!!
    }

    private fun getBranchUpstream(branchName: String): Branch.Upstream? {
        val remote = getRemote(branchName) ?: return null
        val remoteBranchName = FunctionalProcessBuilder.normal()
            .gitCommand()
            .arguments("config", "--get", "branch.$branchName.merge")
            .directory(directory)
            .beginAndWaitFor().result
            ?.map { it.substringAfter("refs/heads/") }
        return Branch.Upstream(remote, remoteBranchName)
    }

    fun getAllBranches(): FunctionalProcessBuilder<Array<Branch>> {
        return FunctionalProcessBuilder {
            val branches = mutableListOf<Branch>()
            it.output.split("\n").forEach {
                if (!it.contains(" -> ")) {
                    branches.add(parseBranchData(it))
                }
            }
            branches.toTypedArray()
        }
            .gitCommand()
            .arguments("branch", "-a", "-vv")
            .directory(directory)
    }

    private fun parseBranchData(verboseBranchData: String): Branch {
        val branchName = verboseBranchData.substringUntil(2, ' ')
        val remote = getRemote(branchName)
        val upstream = if (remote == null) {
            null
        } else {
            parseBranchUpstream(verboseBranchData, remote)
        }
        return Branch(this, branchName, verboseBranchData.startsWith("*"), upstream)
    }

    private fun getRemote(branchName: String): RemoteReference? {
        val remoteName = Remote.getUpstreamName(directory, branchName) ?: return null
        val remoteUrl = Remote.getUrl(directory, remoteName) ?: return null
        return RemoteReference(remoteName, Remote.getRemote(remoteUrl))
    }

    private fun parseBranchUpstream(
        verboseBranchData: String,
        remote: RemoteReference
    ): Branch.Upstream {
        val matcher = REMOTE_BRANCH_PATTERN.matcher(verboseBranchData)
        if (matcher.find()) {
            val upstream = matcher.group(1)
            if (!upstream.endsWith(": gone")) {
                return Branch.Upstream(
                    remote, upstream.replace(remote.name + "/", "")
                )
            }
        }
        return Branch.Upstream(remote, null)
    }

    fun fetch(): ProceduralProcessBuilder {
        return ProceduralProcessBuilder()
            .gitCommand()
            .arguments("fetch")
            .directory(directory)
    }

    fun addToStage(file: File): ProceduralProcessBuilder = ProceduralProcessBuilder()
        .gitCommand()
        .arguments("add", file.canonicalFile.relativeTo(directory).path)
        .directory(directory)

    fun removeFromStage(file: File): ProceduralProcessBuilder = ProceduralProcessBuilder()
        .gitCommand()
        .arguments("reset", "HEAD", "--", file.canonicalFile.relativeTo(directory).path)
        .directory(directory)

    fun getChangedFiles(): FunctionalProcessBuilder<Array<File>> {
        return FunctionalProcessBuilder(this::mapChangedFilesResponse)
            .gitCommand()
            .arguments("diff", "--name-only")
            .directory(directory)
    }

    private fun mapChangedFilesResponse(response: Response): Array<File> {
        return response.output.split("\n").stream()
            .filter { it.isNotEmpty() }
            .map { relativePath -> File(directory.absolutePath + File.separatorChar + relativePath) }
            .toTypedArray()
    }

    fun getDifference(file: File): FunctionalProcessBuilder<Difference> {
        return FunctionalProcessBuilder {
            Difference.parse(it.output)
        }
            .gitCommand()
            .arguments("diff", file.canonicalFile.relativeTo(directory).path)
            .directory(directory)
    }

    @Throws(NoSuchRepositoryException::class)
    private fun throwIfDoesNotExist() {
        if (!repositoryExistsAt(directory)) {
            throw NoSuchRepositoryException("Git repository does not exist at \"${directory.path}\".")
        }
    }

}