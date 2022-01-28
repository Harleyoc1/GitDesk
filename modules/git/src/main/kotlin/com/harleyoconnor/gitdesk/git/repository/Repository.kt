package com.harleyoconnor.gitdesk.git.repository

import com.harleyoconnor.gitdesk.git.gitCommand
import com.harleyoconnor.gitdesk.git.repositoryExistsAt
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.process.FunctionalProcessBuilder
import com.harleyoconnor.gitdesk.util.process.ProceduralProcessBuilder
import com.harleyoconnor.gitdesk.util.process.Response
import com.harleyoconnor.gitdesk.util.substringUntil
import com.harleyoconnor.gitdesk.util.toTypedArray
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author Harley O'Connor
 */
data class Repository @Throws(NoSuchRepositoryException::class) constructor(val directory: Directory) {

    companion object {
        private val REMOTE_BRANCH_PATTERN = Pattern.compile("\\[(.*?)]")
    }

    val name: String by lazy { this.directory.name }

    init {
        this.throwIfDoesNotExist()
    }

    fun getCurrentBranch(): Branch {
        val branchName = this.getCurrentBranchName()
        return Branch(this, branchName)
    }

    private fun getCurrentBranchName(): String {
        return FunctionalProcessBuilder.normal()
            .gitCommand()
            .arguments("branch", "--show-current")
            .directory(directory)
            .beginAndWaitFor().result!!
    }

    fun getBranch(name: String): Branch {
        return Branch(this, name)
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
        return Branch(this, verboseBranchData.substringUntil(2, ' '))
    }

    fun getRemoteBranches(): FunctionalProcessBuilder<Array<RemoteBranch>> {
        return FunctionalProcessBuilder {
            val branches = mutableListOf<RemoteBranch>()
            it.output.split("\n").forEach {
                val line = it.substring(2)
                if (!line.contains(" -> ")) {
                    val remoteName = line.substringBefore('/')
                    val remoteUrl = Remote.getUrl(directory, remoteName) ?: return@forEach
                    branches.add(
                        RemoteBranch(
                            RemoteReference(remoteName, Remote.getRemote(remoteUrl)),
                            line.substringAfter('/')
                        )
                    )
                }
            }
            branches.toTypedArray()
        }
            .gitCommand()
            .arguments("branch", "--remotes")
            .directory(directory)
    }

    fun createBranch(base: Branch, name: String): ProceduralProcessBuilder {
        return if (base.isRemoteBranch())
            fetchRemoteBranch(base.asRemoteBranch(), name)
        else ProceduralProcessBuilder()
            .gitCommand()
            .arguments("branch", name, base.name)
            .directory(directory)
    }

    fun createBranchWithUpstream(base: Branch, name: String, upstreamBranch: RemoteBranch): ProceduralProcessBuilder {
        val upstreamSetter = ProceduralProcessBuilder()
            .gitCommand()
            .arguments(
                "branch",
                "-u",
                "${upstreamBranch.remote.name}/${upstreamBranch.name}",
                name
            )
            .directory(directory)
        val primaryBuilder: ProceduralProcessBuilder = if (base.isRemoteBranch()) {
            fetchRemoteBranch(base.asRemoteBranch(), name)
        } else {
            ProceduralProcessBuilder()
                .gitCommand()
                .arguments("branch", name, base.name)
                .directory(directory)
        }.ifSuccessful {
            upstreamSetter.begin()
        }
        return primaryBuilder
    }

    fun fetchRemoteBranch(
        base: RemoteBranch,
        name: String
    ): ProceduralProcessBuilder {
        return ProceduralProcessBuilder()
            .gitCommand()
            .arguments("fetch", base.remote.name, "${base.name}:$name")
            .directory(directory)
    }

    fun hasBranch(name: String): Boolean {
        return ProceduralProcessBuilder()
            .gitCommand()
            .arguments("rev-parse", "--verify", "--quiet", name)
            .directory(directory)
            .beginAndWaitFor().code == 0
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

    fun getChangedFiles(): FunctionalProcessBuilder<Array<ChangedFile>> {
        return FunctionalProcessBuilder(this::mapStatusResponse)
            .gitCommand()
            .arguments("status", "--porcelain=v1")
            .directory(directory)
    }

    private fun mapStatusResponse(response: Response): Array<ChangedFile> {
        return response.map {
            it.output.split("\n").mapNotNull { line ->
                ChangedFile.parseChangedFile(line, directory)
            }.toTypedArray()
        }.result!!
    }

    fun stageAllFiles(): ProceduralProcessBuilder {
        return ProceduralProcessBuilder()
            .gitCommand()
            .arguments("add", ".")
            .directory(directory)
    }

    fun unStageAllFiles(): ProceduralProcessBuilder {
        return ProceduralProcessBuilder()
            .gitCommand()
            .arguments("reset", "HEAD", "--", ".")
            .directory(directory)
    }

    fun getUnStagedFiles(): FunctionalProcessBuilder<Array<File>> {
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

    fun commit(summary: String, description: String): ProceduralProcessBuilder {
        return ProceduralProcessBuilder()
            .gitCommand()
            .arguments("commit", "-m", summary, "-m", description)
            .directory(directory)
    }

    @Throws(NoSuchRepositoryException::class)
    private fun throwIfDoesNotExist() {
        if (!repositoryExistsAt(directory)) {
            throw NoSuchRepositoryException("Git repository does not exist at \"${directory.path}\".")
        }
    }

    class ChangedFile(
        val file: File,
        val staged: Boolean,
        val changeType: ChangeType
    ) {
        enum class ChangeType {
            MODIFIED, ADDED;

            companion object {
                fun getFor(char: Char): ChangeType? {
                    return when (char) {
                        'M' -> MODIFIED
                        'A' -> ADDED
                        else -> null
                    }
                }
            }
        }

        companion object {
            private val CHANGED_FILE_STATUS_PATTERN = Pattern.compile("([MA ])([MA ]) (.*)")

            /**
             * Parses a line from the status command's porcelain v1 output.
             */
            fun parseChangedFile(line: String, repositoryDirectory: Directory): ChangedFile? {
                val matcher = CHANGED_FILE_STATUS_PATTERN.matcher(line)
                if (matcher.find()) {
                    return parseFromGroups(matcher, repositoryDirectory)
                }
                throw RuntimeException("Could not parse changed file line: $line")
            }

            private fun parseFromGroups(
                matcher: Matcher,
                repositoryDirectory: Directory
            ): ChangedFile? {
                var staged = true
                var state = matcher.group(1)[0]
                if (state == ' ') {
                    staged = false
                    state = matcher.group(2)[0]
                }
                val relativePath = matcher.group(3)
                return ChangedFile(
                    File(repositoryDirectory.absolutePath + File.separatorChar + relativePath),
                    staged,
                    ChangeType.getFor(state) ?: return null
                )
            }
        }
    }

}