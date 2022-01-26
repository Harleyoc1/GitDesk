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
        .thenApply {
            Response.from(it).map { json -> RegistrationData.ADAPTER.fromJson(json) }
        }
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
        .thenApply { Response.from(it).map { json -> Session.ADAPTER.fromJson(json) } }
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

class AccountCredentials(
    private val username: String,
    private val password: String
) {
    fun createMap(): Map<String, String> {
        return mapOf(
            "username" to username,
            "password" to password
        )
    }
}

fun signInRequest(accountCredentials: AccountCredentials): CompletableFuture<Response<Session>> {
    val request = createSignInRequest(accountCredentials)
    return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply { Response.from(it).map { json -> Session.ADAPTER.fromJson(json) } }
}

private fun createSignInRequest(accountCredentials: AccountCredentials): HttpRequest {
    return HttpRequest.newBuilder()
        .POST(
            HttpRequest.BodyPublishers.ofString(
                accountCredentials.createMap().toHttpFormData()
            )
        )
        .uri(
            startBuildingUri()
                .append("sign-in/")
                .build()
        )
        .header(HttpHeader.CONTENT_TYPE, HttpHeader.FORM_ENCODED)
        .header(HttpHeader.ACCEPT, HttpHeader.JSON)
        .build()
}

fun getAccountRequest(session: Session): CompletableFuture<Response<Account>> {
    val request = createGetAccountRequest(session)
    return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply { Response.from(it).map { json -> Account.ADAPTER.fromJson(json) } }
}

private fun createGetAccountRequest(session: Session): HttpRequest {
    return HttpRequest.newBuilder()
        .GET()
        .uri(
            startBuildingUri()
                .append("account/")
                .build()
        )
        .header(HttpHeader.SESSION_KEY, session.key)
        .header(HttpHeader.ACCEPT, HttpHeader.JSON)
        .build()
}

fun deleteSessionRequest(session: Session): CompletableFuture<Response<Unit>> {
    val request = createDeleteSessionRequest(session)
    return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply { Response.from(it).map { } }
}

private fun createDeleteSessionRequest(session: Session): HttpRequest {
    return HttpRequest.newBuilder()
        .DELETE()
        .uri(
            startBuildingUri()
                .append("session/")
                .parameter("session_key", session.key)
                .build()
        )
        .header(HttpHeader.ACCEPT, HttpHeader.JSON)
        .build()
}

fun getGitHubAccountRequest(session: Session): CompletableFuture<Response<GitHubAccount>> {
    val request = createGetGitHubAccountRequest(session)
    return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply { Response.from(it).map { json -> GitHubAccount.ADAPTER.fromJson(json) } }
}

private fun createGetGitHubAccountRequest(session: Session): HttpRequest {
    return HttpRequest.newBuilder()
        .GET()
        .uri(
            startBuildingUri()
                .append("link/github/credentials/")
                .build()
        )
        .header(HttpHeader.SESSION_KEY, session.key)
        .header(HttpHeader.ACCEPT, HttpHeader.JSON)
        .build()
}

class GitHubLinkingData(
    val username: String,
    val state: String,
    val code: String
) {
    companion object {
        val ADAPTER: JsonAdapter<GitHubLinkingData> = MOSHI.adapter(GitHubLinkingData::class.java)
    }
}

fun linkGitHubRequest(username: String, session: Session): CompletableFuture<Response<GitHubLinkingData>> {
    val request = createLinkGitHubRequest(username, session)
    return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply { Response.from(it).map { json -> GitHubLinkingData.ADAPTER.fromJson(json) } }
}

private fun createLinkGitHubRequest(username: String, session: Session): HttpRequest {
    return HttpRequest.newBuilder()
        .POST(
            HttpRequest.BodyPublishers.ofString(
                mapOf("username" to username).toHttpFormData()
            )
        )
        .uri(
            startBuildingUri()
                .append("link/github/")
                .build()
        )
        .header(HttpHeader.SESSION_KEY, session.key)
        .header(HttpHeader.CONTENT_TYPE, HttpHeader.FORM_ENCODED)
        .header(HttpHeader.ACCEPT, HttpHeader.JSON)
        .build()
}

fun unlinkGitHubRequest(session: Session): CompletableFuture<Response<Unit>> {
    val request = createUnlinkGitHubRequest(session)
    return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply { Response.from(it).map { } }
}

private fun createUnlinkGitHubRequest(session: Session): HttpRequest {
    return HttpRequest.newBuilder()
        .DELETE()
        .uri(
            startBuildingUri()
                .append("link/github/")
                .build()
        )
        .header(HttpHeader.SESSION_KEY, session.key)
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