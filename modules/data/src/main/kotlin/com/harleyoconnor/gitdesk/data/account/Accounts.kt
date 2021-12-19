package com.harleyoconnor.gitdesk.data.account

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.util.network.CLIENT
import com.harleyoconnor.gitdesk.util.network.HttpHeader
import com.harleyoconnor.gitdesk.util.network.Response
import com.harleyoconnor.gitdesk.util.network.URIBuilder
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors

fun isUsernameAvailableQuery(username: String): CompletableFuture<Response<Boolean>> {
    val request = createIsUsernameAvailableQueryRequest(username)
    return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply { httpResponse -> Response.from(httpResponse).map { httpResponse.statusCode() != 210 } }
}

private fun createIsUsernameAvailableQueryRequest(username: String): HttpRequest {
    return HttpRequest.newBuilder()
        .GET()
        .uri(
            startBuildingUri()
                .append("register/verify/username-available/")
                .parameter("username", username)
                .build()
        )
        .build()
}

class AccountCreationData(
    private val username: String,
    val email: String,
    private val password: String
) {
    fun createMap(): Map<String, String> {
        return mapOf(
            "username" to username,
            "email" to email,
            "password" to password
        )
    }
}

class RegistrationData(
    @Json(name = "client_code") val clientCode: String
) {
    companion object {
        val ADAPTER: JsonAdapter<RegistrationData> = MOSHI.adapter(RegistrationData::class.java)
    }
}

fun registerRequest(creationData: AccountCreationData): CompletableFuture<Response<RegistrationData>> {
    val request = createRegisterRequest(creationData)
    return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply { Response.from(it).map { json -> RegistrationData.ADAPTER.fromJson(json) } }
}

private fun createRegisterRequest(creationData: AccountCreationData): HttpRequest {
    return HttpRequest.newBuilder()
        .POST(
            HttpRequest.BodyPublishers.ofString(
                creationData.createMap().toHttpFormData()
            )
        )
        .uri(
            startBuildingUri()
                .append("register/")
                .build()
        )
        .header(HttpHeader.CONTENT_TYPE, HttpHeader.FORM_ENCODED)
        .header(HttpHeader.ACCEPT, HttpHeader.JSON)
        .build()
}

class VerificationData(
    private val email: String,
    private val clientCode: String,
    var verificationCode: String? = null
) {
    fun createMap(): Map<String, String> {
        return mapOf(
            "email" to email,
            "client_code" to clientCode,
            "verification_code" to verificationCode.toString()
        )
    }
}

fun verifyEmailRequest(verificationData: VerificationData): CompletableFuture<Response<Session>> {
    assert(verificationData.verificationCode != null)
    val request = createVerifyEmailRequest(verificationData)
    return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply { println(it.statusCode()); Response.from(it).map { json -> println(json); Session.ADAPTER.fromJson(json) } }
}

private fun createVerifyEmailRequest(verificationData: VerificationData): HttpRequest {
    return HttpRequest.newBuilder()
        .POST(
            HttpRequest.BodyPublishers.ofString(
                verificationData.createMap().toHttpFormData()
            )
        )
        .uri(
            startBuildingUri()
                .append("register/verify/email/")
                .build()
        )
        .header(HttpHeader.CONTENT_TYPE, HttpHeader.FORM_ENCODED)
        .header(HttpHeader.ACCEPT, HttpHeader.JSON)
        .build()
}

private fun startBuildingUri(): URIBuilder {
    return URIBuilder()
        .https()
        .append("harleyoconnor.com/gitdesk/")
}

private fun <K, V> Map<K, V>.toHttpFormData(): String {
    return this.entries.stream()
        .map { "${it.key}=${it.value}" }
        .collect(Collectors.joining("&"))
}