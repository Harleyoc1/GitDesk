package com.harleyoconnor.gitdesk.util.process

/**
 * @author Harley O'Connor
 */
abstract class AbstractExecution<R : Response>(
    protected val command: List<String>,
    protected val process: Process,
    private val ifSuccessActions: Array<(R) -> Unit>,
    private val ifFailActions: Array<(R) -> Unit>
) : Execution<R> {

    private var response: R? = null

    override fun isFinished(): Boolean {
        return !process.isAlive
    }

    override fun getResponse(): R? {
        return response
    }

    protected fun setResponse(response: R) {
        this.response = response
    }

    protected fun succeeded() {
        ifSuccessActions.forEach {
            it(response!!)
        }
    }

    protected fun failed() {
        ifFailActions.forEach {
            it(response!!)
        }
    }
}