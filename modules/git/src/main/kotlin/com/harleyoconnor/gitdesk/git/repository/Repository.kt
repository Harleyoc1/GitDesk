package com.harleyoconnor.gitdesk.git.repository

import com.harleyoconnor.gitdesk.git.gitCommand
import com.harleyoconnor.gitdesk.git.repositoryExistsAt
import com.harleyoconnor.gitdesk.util.Directory
import com.harleyoconnor.gitdesk.util.process.FunctionalProcessBuilder
import com.harleyoconnor.gitdesk.util.process.ProceduralProcessBuilder
import com.harleyoconnor.gitdesk.util.process.Response
import com.harleyoconnor.gitdesk.util.toTypedArray
import java.io.File
import java.util.Queue

/**
 * @author Harley O'Connor
 */
data class Repository @Throws(NoSuchRepositoryException::class) constructor(val directory: Directory) {

    val name: String by lazy { this.directory.name }

    init {
        this.throwIfDoesNotExist()
    }

    fun getCurrentBranch(): Branch {
        return Branch(this, this.getCurrentBranchName())
    }

    private fun getCurrentBranchName(): String {
        return FunctionalProcessBuilder.normal()
            .gitCommand()
            .arguments("branch", "--show-current")
            .directory(directory)
            .beginAndWaitFor().result!!
    }

    fun fetch(): ProceduralProcessBuilder {
        return ProceduralProcessBuilder()
            .gitCommand()
            .arguments("fetch")
            .directory(directory)
    }

    fun getChangedFiles(): FunctionalProcessBuilder<Array<File>> {
        return FunctionalProcessBuilder(this::mapChangedFilesResponse)
            .gitCommand()
            .arguments("diff", "--name-only")
            .directory(directory)
    }

    private fun mapChangedFilesResponse(response: Response): Array<File> {
        return response.output.split("\n").stream()
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

    private fun nextHeaderLine(lines: Queue<String>) {
        for (line in lines) {
            if (line.startsWith("@@") && line.endsWith("@@")) {
                return
            }
            lines.remove(line)
        }
    }

    @Throws(NoSuchRepositoryException::class)
    private fun throwIfDoesNotExist() {
        if (!repositoryExistsAt(directory)) {
            throw NoSuchRepositoryException("Git repository does not exist at \"${directory.path}\".")
        }
    }

}