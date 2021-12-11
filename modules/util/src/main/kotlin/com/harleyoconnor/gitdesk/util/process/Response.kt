package com.harleyoconnor.gitdesk.util.process

/**
 * @author Harley O'Connor
 */
open class Response(
    val code: Int,
    val output: String,
    val error: String
) {

    fun success() = this.code == 0

    fun <R> map(mapper: (Response) -> R?): FunctionalResponse<R> {
        return FunctionalResponse(mapper(this), this.code, this.output, this.error)
    }

}

class FunctionalResponse<R>(
    val result: R?,
    code: Int,
    output: String,
    error: String
) : Response(code, output, error)