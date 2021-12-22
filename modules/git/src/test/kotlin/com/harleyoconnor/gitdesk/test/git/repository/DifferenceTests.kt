package com.harleyoconnor.gitdesk.test.git.repository

import com.harleyoconnor.gitdesk.git.repository.Repository
import com.harleyoconnor.gitdesk.util.Directory
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author Harley O'Connor
 */
class DifferenceTests {

    @Disabled("Currently only prints local change data.")
    @Test
    fun `test getting list of changed files`() {
        val repository = Repository(Directory(File(".").canonicalFile.parentFile.parentFile))
        println(
            repository.getChangedFiles().beginAndWaitFor(30, TimeUnit.SECONDS).result?.contentToString()
        )
    }

    @Disabled("Currently only prints local change data.")
    @Test
    fun `test parsing difference`() {
        val repository = Repository(Directory(File(".").canonicalFile.parentFile.parentFile))
        repository.getChangedFiles().beginAndWaitFor(30, TimeUnit.SECONDS).result?.let {
            it.forEach { changedFile ->
                println(repository.getDifference(changedFile).beginAndWaitFor(30, TimeUnit.SECONDS).result)
            }
        }
    }

}