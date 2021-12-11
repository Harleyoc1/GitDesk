package com.harleyoconnor.gitdesk.test.git.repository

import com.harleyoconnor.gitdesk.git.repository.NoSuchRepositoryException
import com.harleyoconnor.gitdesk.git.repository.Repository
import com.harleyoconnor.gitdesk.util.Directory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

/**
 * @author Harley O'Connor
 */
class RepositoryTests {

    @Test
    fun `test does not throw when constructing valid repository`() {
        val currentDirectory = Directory(File("."))

        Assertions.assertDoesNotThrow {
            Repository(currentDirectory)
        }
    }

    @Test
    fun `test throws constructing invalid repository`() {
        val rootDirectory = Directory(File("/")) // Assuming the root directory is not a Git repository.

        Assertions.assertThrows(NoSuchRepositoryException::class.java) {
            Repository(rootDirectory)
        }
    }

    @Test
    fun `test fetch`() {
        val repository = Repository(Directory(File(".")))

        Assertions.assertDoesNotThrow {
            repository.fetch()
        }
    }

}