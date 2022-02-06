package com.harleyoconnor.gitdesk.test.git

import com.harleyoconnor.gitdesk.util.getUserHome
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * @author Harley O'Connor
 */
class ProcessTests {

    @Test
    fun `test processes`() {
        val process = ProcessBuilder()
            .command("git", "push", "origin")
            .directory(File("/Users/harleyoconnor/GitDesk-Test"))
            .inheritIO()
            .redirectErrorStream(true)
            .start()

        val standardIn = process.outputStream
        val standardOut = process.inputStream

        val inWriter = BufferedWriter(OutputStreamWriter(standardIn))
        val outReader = BufferedReader(InputStreamReader(standardOut))


        var line: String? = outReader.readLine()
        while (line != null) {
            println(line)
            line = outReader.readLine()
        }
    }

}