package com.harleyoconnor.gitdesk.util.process

/**
 * @author Harley O'Connor
 */
class ProceduralExecution(
    command: List<String>,
    process: Process,
    ifSuccessActions: Array<(Response) -> Unit>,
    ifFailActions: Array<(Response) -> Unit>
) : AbstractExecution<Response>(command, process, ifSuccessActions, ifFailActions) {

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