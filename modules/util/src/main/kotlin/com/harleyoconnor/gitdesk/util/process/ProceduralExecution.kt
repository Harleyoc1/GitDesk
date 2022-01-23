package com.harleyoconnor.gitdesk.util.process

/**
 * @author Harley O'Connor
 */
class ProceduralExecution(
    command: List<String>,
    process: Process,
    ifSuccessAction: (Response) -> Unit,
    ifFailAction: (Response) -> Unit
) : AbstractExecution<Response>(command, process, ifSuccessAction, ifFailAction) {

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
        this.setResponse(response)
        if (response.success()) {
            succeeded()
        } else {
            failed()
        }
    }

}