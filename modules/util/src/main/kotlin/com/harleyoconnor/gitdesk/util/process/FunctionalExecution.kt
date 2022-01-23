package com.harleyoconnor.gitdesk.util.process

/**
 * @author Harley O'Connor
 */
class FunctionalExecution<R>(
    command: List<String>,
    process: Process,
    private val resultFunction: (Response) -> R,
    ifSuccessAction: (FunctionalResponse<R>) -> Unit,
    ifFailAction: (FunctionalResponse<R>) -> Unit
) : AbstractExecution<FunctionalResponse<R>>(command, process, ifSuccessAction, ifFailAction) {

    init {
        process.onExit().thenRun { this.afterFinished() }
    }

    private fun afterFinished() {
        val response = Response(
            command.joinToString(" "),
            process.exitValue(),
            process.readOutput(),
            process.readError()
        )
        if (response.success()) {
            setResponse(response.map(resultFunction))
            succeeded()
        } else {
            setResponse(response.map { null })
            failed()
        }
    }

}