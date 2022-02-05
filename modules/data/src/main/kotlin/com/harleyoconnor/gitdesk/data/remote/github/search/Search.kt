package com.harleyoconnor.gitdesk.data.remote.github.search

import com.harleyoconnor.gitdesk.data.account.Session
import com.harleyoconnor.gitdesk.data.remote.github.GitHubNetworking
import com.harleyoconnor.gitdesk.util.network.CLIENT
import com.harleyoconnor.gitdesk.util.network.HttpHeader
import com.harleyoconnor.gitdesk.util.network.URIBuilder
import com.harleyoconnor.gitdesk.util.network.mapOrElseThrow
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 *
 * @author Harley O'Connor
 */
abstract class Search<E>(
    private val query: String,
    private val sort: String = "best match",
    private val order: String = "desc",
    private val perPage: Int = 5,
    private val page: Int = 1,
    private val executor: Executor = Executors.newSingleThreadExecutor()
) {

    protected abstract val id: String

    fun run(): CompletableFuture<SearchResults<E>> = this.sendRequest()
        .thenApply {
            it.mapOrElseThrow(this::decodeResults) {
                "Searching GitHub for $id."
            }
        }

    protected abstract fun decodeResults(body: String): SearchResults<E>?

    private fun responseSuccessful(response: HttpResponse<String>): Boolean {
        return response.statusCode() in 200..299
    }

    private fun sendRequest(): CompletableFuture<HttpResponse<String>> {
        return CLIENT.sendAsync(
            HttpRequest.newBuilder()
                .GET()
                .uri(this.buildUri())
                .setHeader(HttpHeader.ACCEPT, HttpHeader.JSON)
                .setHeader(HttpHeader.AUTHORIZATION, this.getAuthHeader())
                .build(),
            HttpResponse.BodyHandlers.ofString()
        )
    }

    private fun getAuthHeader(): String {
        val account = Session.getOrLoad()?.getGitHubAccount() ?: return ""
        return "token " + account.accessToken
    }

    private fun buildUri(): URI = URIBuilder()
        .append(GitHubNetworking.url)
        .append("/search/")
        .append(this.id)
        .parameter("q", query)
        .parameter("sort", sort)
        .parameter("order", order)
        .parameter("per_page", perPage.toString())
        .parameter("page", page.toString())
        .build()


}