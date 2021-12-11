package com.harleyoconnor.gitdesk.util.process

/**
 * Holds info about the execution of a [Process].
 *
 * @author Harley O'Connor
 */
interface Execution<R : Response> {

    fun isFinished(): Boolean

    fun getResponse(): R?

}