package com.harleyoconnor.gitdesk.data.remote.github.search

import com.harleyoconnor.gitdesk.data.remote.github.GitHubNetworking
import com.harleyoconnor.gitdesk.util.network.CLIENT
import com.harleyoconnor.gitdesk.util.network.HttpHeader
import com.harleyoconnor.gitdesk.util.network.URIBuilder
import org.apache.logging.log4j.LogManager
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 *
 * @author Harley O'Connor
 */
abstract class Search<E>(
    private val query: String,
    private val sort: String = "best match",
    private val order: String = "desc",
    private val perPage: Int = 5,
    private val page: Int = 1
) {

    protected abstract val id: String

    fun run(): SearchResults<E>? {
        val response = this.sendRequest()

        return if (this.responseSuccessful(response)) {
            this.decodeResults(response.body())
        } else {
            LogManager.getLogger()
                .error("Received ${response.statusCode()} from search request. Result: ${response.body()}")
            null
        }
    }

    protected abstract fun decodeResults(body: String): SearchResults<E>?

    private fun responseSuccessful(response: HttpResponse<String>): Boolean {
        return response.statusCode() in 200..299
    }

    private fun sendRequest(): HttpResponse<String> {
        return CLIENT.send(
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
        return ""
        // TODO: Re-implement local auth data
//        val account = Account.get() ?: return ""
//        return "token " + account.gitHubAccessToken
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