package com.harleyoconnor.gitdesk.util.process

import com.harleyoconnor.gitdesk.util.Directory
import java.util.concurrent.TimeUnit

/**
 * @author Harley O'Connor
 */
interface ProcessBuilder<R : Response> {

    fun command(command: String): ProcessBuilder<R>

    fun arguments(vararg arguments: String): ProcessBuilder<R>

    fun directory(directory: Directory): ProcessBuilder<R>

    fun ifSuccessful(action: (R) -> Unit): ProcessBuilder<R>

    fun ifFailure(action: (R) -> Unit): ProcessBuilder<R>

    fun begin(): Execution<R>

    fun beginAndWaitFor(): R

    fun beginAndWaitFor(timeout: Long, unit: TimeUnit): R

}