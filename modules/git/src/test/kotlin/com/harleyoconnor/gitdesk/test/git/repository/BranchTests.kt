package com.harleyoconnor.gitdesk.test.git.repository

import com.harleyoconnor.gitdesk.git.repository.Repository
import com.harleyoconnor.gitdesk.util.Directory
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

/**
 * @author Harley O'Connor
 */
class BranchTests {

    @Disabled("Currently only prints the current branch.")
    @Test
    fun `test getting current branch`() {
        val repository = Repository(Directory(File(".")))
        println(repository.getCurrentBranch())
    }

}