package com.harleyoconnor.gitdesk.util.process

import com.harleyoconnor.gitdesk.util.Directory
import java.util.concurrent.TimeUnit

/**
 * @author Harley O'Connor
 */
@Suppress("UNCHECKED_CAST")
abstract class AbstractProcessBuilder<R : Response, B : AbstractProcessBuilder<R, B>> : ProcessBuilder<R> {

    protected val builder: java.lang.ProcessBuilder = ProcessBuilder()

    protected var ifSuccessAction: (R) -> Unit = {}
    protected var ifFailAction: (R) -> Unit = {}

    override fun command(command: String): B {
        builder.command().add(0, command)
        return this as B
    }

    override fun arguments(vararg arguments: String): B {
        builder.command().addAll(arguments)
        return this as B
    }

    override fun directory(directory: Directory): B {
        builder.directory(directory)
        return this as B
    }

    override fun ifSuccessful(action: (R) -> Unit): B {
        ifSuccessAction = action
        return this as B
    }

    override fun ifFailure(action: (R) -> Unit): B {
        ifFailAction = action
        return this as B
    }

    protected fun executeProcess(): Response {
        val command = builder.command().joinToString(" ")
        val process = builder.start()
        process.waitFor()
        return Response(
            command,
            process.exitValue(),
            process.readOutput(),
            process.readError()
        )
    }

    protected fun executeProcess(timeout: Long, unit: TimeUnit): Response {
        val command = builder.command().joinToString(" ")
        val process = builder.start()
        process.waitFor(timeout, unit)
        return Response(
            command,
            process.exitValue(),
            process.readOutput(),
            process.readError()
        )
    }

}