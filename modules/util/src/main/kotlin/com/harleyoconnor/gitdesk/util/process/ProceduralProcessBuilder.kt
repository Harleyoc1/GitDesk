package com.harleyoconnor.gitdesk.util.process

import java.util.concurrent.TimeUnit

/**
 * @author Harley O'Connor
 */
class ProceduralProcessBuilder : AbstractProcessBuilder<Response, ProceduralProcessBuilder>() {

    override fun begin(): ProceduralExecution {
        return ProceduralExecution(
            builder.command(),
            builder.start(),
            ifSuccessActions.toTypedArray(),
            ifFailActions.toTypedArray()
        )
    }

    override fun beginAndWaitFor(): Response {
        val response = executeProcess()
        if (response.success()) {
            succeeded(response)
        } else {
            failed(response)
        }
        return response
    }

    override fun beginAndWaitFor(timeout: Long, unit: TimeUnit): Response {
        val response = executeProcess(timeout, unit)
        if (response.success()) {
            succeeded(response)
        } else {
            failed(response)
        }
        return response
    }

    private fun succeeded(response: Response) {
        ifSuccessActions.forEach {
            it(response)
        }
    }

    private fun failed(response: Response) {
        ifFailActions.forEach {
            it(response)
        }
    }

}