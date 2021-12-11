package com.harleyoconnor.gitdesk.test.git

import com.harleyoconnor.gitdesk.git.repositoryExistsAt
import com.harleyoconnor.gitdesk.util.Directory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URL

/**
 * @author Harley O'Connor
 */
class GitTests {

    @Test
    fun `test checking for local repository's existence`() {
        val currentDirectory = Directory(File("."))
        Assertions.assertTrue(repositoryExistsAt(currentDirectory))
    }

    @Test
    fun `test checking for public remote repository's existence`() {
        val repositoryUrl = URL("https://github.com/Harleyoc1/JavaUtilities")
        Assertions.assertTrue(repositoryExistsAt(repositoryUrl))
    }

    @Disabled("Testing for private remote repositories requires authentication.")
    @Test
    fun `test checking for private remote repository's existence`() {
        val repositoryUrl = URL("https://github.com/Harleyoc1/GitDesk-old")
        Assertions.assertTrue(repositoryExistsAt(repositoryUrl))
    }

}