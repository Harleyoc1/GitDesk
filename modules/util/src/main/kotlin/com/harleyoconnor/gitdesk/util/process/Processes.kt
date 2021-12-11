package com.harleyoconnor.gitdesk.util.process

import java.io.InputStreamReader

internal fun Process.readOutput(): String {
    val reader = InputStreamReader(this.inputStream)
    val output = reader.readText()
    reader.close()
    return output.replace("\n", "")
}

internal fun Process.readError(): String {
    val reader = InputStreamReader(this.errorStream)
    val error = reader.readText()
    reader.close()
    return error.replace("\n", "")
}
