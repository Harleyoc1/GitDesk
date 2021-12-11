package com.harleyoconnor.gitdesk.util.process

import java.util.concurrent.TimeUnit

/**
 * @author Harley O'Connor
 */
class FunctionalProcessBuilder<R>(
    private val resultFunction: (Response) -> R
) : AbstractProcessBuilder<FunctionalResponse<R>, FunctionalProcessBuilder<R>>() {

    companion object {
        fun normal(): FunctionalProcessBuilder<String> {
            return FunctionalProcessBuilder { it.output }
        }
    }

    override fun begin(): FunctionalExecution<R> {
        return FunctionalExecution(builder.start(), resultFunction, ifSuccessAction, ifFailAction)
    }

    override fun beginAndWaitFor(): FunctionalResponse<R> {
        val response = executeProcess()
        return if (response.success()) {
            succeeded(response)
        } else {
            failed(response)
        }
    }

    override fun beginAndWaitFor(timeout: Long, unit: TimeUnit): FunctionalResponse<R> {
        val response = executeProcess(timeout, unit)
        return if (response.success()) {
            succeeded(response)
        } else {
            failed(response)
        }
    }

    private fun succeeded(response: Response): FunctionalResponse<R> {
        val functionalResponse = response.map(resultFunction)
        ifSuccessAction(functionalResponse)
        return functionalResponse
    }

    private fun failed(response: Response): FunctionalResponse<R> {
        val functionalResponse: FunctionalResponse<R> = response.map { null }
        ifFailAction(functionalResponse)
        return functionalResponse
    }

}