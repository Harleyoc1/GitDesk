package com.harleyoconnor.gitdesk.util.network

import java.net.http.HttpResponse

/**
 *
 * @author Harley O'Connor
 */
class Response<B>(
    private val body: B?,
    private val code: Int
) {

    companion object {
        fun <B> from(response: HttpResponse<B>): Response<B> {
            return Response(response.body(), response.statusCode())
        }
    }

    fun <N> map(mapper: (B) -> N?): Response<N> {
        return Response(body?.let { mapper(it) }, code)
    }

    fun ifCodeEquals(code: Int, action: () -> Unit): Response<B> {
        if (this.code == code) {
            action()
        }
        return this
    }

    fun ifCodeIn(range: IntRange, action: () -> Unit): Response<B> {
        if (this.code in range) {
            action()
        }
        return this
    }

    fun apply(consumer: (B) -> Unit) {
        if (wasSuccess()) {
            consumer(body!!)
        }
    }

    fun <T> apply(function: (B) -> T, default: T): T {
        return if (wasSuccess()) {
            function(body!!)
        } else default
    }

    fun wasSuccess() = code in 200 until 300 && body != null

    fun wasError() = code !in 200 until 300 || body == null

    fun get(): B? = body

    fun getCode(): Int = code
}