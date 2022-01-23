package com.harleyoconnor.gitdesk.util.process

/**
 * @author Harley O'Connor
 */
open class Response(
    val input: String,
    val code: Int,
    val output: String,
    val error: String
) {

    fun success() = this.code == 0

    fun <R> map(mapper: (Response) -> R?): FunctionalResponse<R> {
        return FunctionalResponse(mapper(this), this.input, this.code, this.output, this.error)
    }

}

class FunctionalResponse<R>(
    val result: R?,
    input: String,
    code: Int,
    output: String,
    error: String
) : Response(input, code, output, error)