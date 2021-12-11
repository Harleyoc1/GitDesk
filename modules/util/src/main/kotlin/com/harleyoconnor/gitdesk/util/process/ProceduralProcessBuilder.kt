package com.harleyoconnor.gitdesk.util.process

import java.util.concurrent.TimeUnit

/**
 * @author Harley O'Connor
 */
class ProceduralProcessBuilder : AbstractProcessBuilder<Response, ProceduralProcessBuilder>() {

    override fun begin(): ProceduralExecution {
        return ProceduralExecution(builder.start(), ifSuccessAction, ifFailAction)
    }

    override fun beginAndWaitFor(): Response {
        val response = executeProcess()
        if (response.success()) {
            ifSuccessAction(response)
        } else {
            ifFailAction(response)
        }
        return response
    }

    override fun beginAndWaitFor(timeout: Long, unit: TimeUnit): Response {
        val response = executeProcess(timeout, unit)
        if (response.success()) {
            ifSuccessAction(response)
        } else {
            ifFailAction(response)
        }
        return response
    }

}