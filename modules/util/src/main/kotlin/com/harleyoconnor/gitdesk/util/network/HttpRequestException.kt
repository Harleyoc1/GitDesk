package com.harleyoconnor.gitdesk.util.network

/**
 * Thrown to indicate a HTTP request failed.
 *
 * @author Harley O'Connor
 */
class HttpRequestException(
    message: String,
    val statusCode: Int,
    val body: Any?
) : Exception(message) {

    override fun toString(): String {
        return "HttpRequestException(message=$message, statusCode=$statusCode, body=$body)"
    }
}