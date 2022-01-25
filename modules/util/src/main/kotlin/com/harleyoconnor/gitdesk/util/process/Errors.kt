package com.harleyoconnor.gitdesk.util.process

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.util.StackLocatorUtil

fun logFailure(response: Response) {
    LogManager.getLogger(StackLocatorUtil.getCallerClass(2))
        .error("Process execution failed with exit code ${response.code}." +
                "\nInput: ${response.input}" +
                "\nOutput: ${response.output}" +
                "\nError: ${response.error}")
}