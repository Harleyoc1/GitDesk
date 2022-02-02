package com.harleyoconnor.gitdesk.data.remote.github

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.account.GitHubAccount
import com.harleyoconnor.gitdesk.data.account.Session
import com.harleyoconnor.gitdesk.data.remote.Comment
import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.Label
import com.harleyoconnor.gitdesk.data.remote.License
import com.harleyoconnor.gitdesk.data.remote.Platform
import com.harleyoconnor.gitdesk.data.remote.PlatformNetworking
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.RemoteRepositoryReference
import com.harleyoconnor.gitdesk.data.remote.User
import com.harleyoconnor.gitdesk.data.remote.github.timeline.GitHubTimelineAdapter
import com.harleyoconnor.gitdesk.data.remote.timeline.Timeline
import com.harleyoconnor.gitdesk.data.serialisation.adapter.toJson
import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.util.indexOf
import com.harleyoconnor.gitdesk.util.network.CLIENT
import com.harleyoconnor.gitdesk.util.network.HttpHeader
import com.harleyoconnor.gitdesk.util.network.HttpRequestException
import com.harleyoconnor.gitdesk.util.network.PATCH
import com.harleyoconnor.gitdesk.util.network.URIBuilder
import com.harleyoconnor.gitdesk.util.network.getJsonAt
import com.harleyoconnor.gitdesk.util.network.mapOrElseThrow
import com.harleyoconnor.gitdesk.util.network.sendRequest
import com.squareup.moshi.Types
import java.net.URI
import java.net.URL
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture
import java.util.regex.Pattern

object GitHubNetworking : PlatformNetworking {

    private const val acceptHeader = "application/vnd.github.v3+json"

    const val url = "https://api.github.com"
    const val acceptableUsernameRange = "A-Za-z0-9-"
    const val acceptableRepositoryRange = "$acceptableUsernameRange\\_."

    private val repositoryUrlPattern =
        Pattern.compile("http(s?)://(.*@)?(www\\.)?github\\.com/[$acceptableUsernameRange]{4,}/[$acceptableRepositoryRange]+/?")

    override fun canConnect(): Boolean {
        return getJsonAt(URI.create(url)).statusCode() in 200 until 300
    }

    override fun getUser(username: String): User? {
        val response = getJsonAt(URI.create(getUserUrl(username)))
        return if (response.statusCode() in 200 until 300) {
            GitHubUser.ADAPTER.fromJson(response.body())
        } else null
    }

    private fun getUserUrl(username: String) = "$url/users/$username"

    override fun getRemoteRepository(username: String, repository: String): RemoteRepository? {
        val response = getJsonAt(URI.create(getRemoteRepositoryUrl(username, repository)))
        return if (response.statusCode() in 200 until 300) {
            GitHubRemoteRepository.ADAPTER.fromJson(response.body())
        } else null
    }

    private fun getRemoteRepositoryUrl(username: String, repository: String) = "$url/repos/$username/$repository"

    override fun getRemoteRepository(url: URL): RemoteRepository? {
        val name = extractRemoteRepositoryName(url)
        return getRemoteRepository(name.ownerName, name.repositoryName)
    }

    override fun getRemoteRepositoryReference(url: URL): RemoteRepositoryReference {
        val name = extractRemoteRepositoryName(url)
        return RemoteRepositoryReference(url, Platform.GITHUB, name)
    }

    private fun extractRemoteRepositoryName(url: URL): RemoteRepository.Name {
        assert(repositoryUrlPattern.matcher(url.toExternalForm()).matches()) {
            IllegalArgumentException("$url not a valid GitHub repository URL.")
        }
        return extractRemoteRepositoryName(url.toExternalForm().removeSuffix("/").removeSuffix(".git"))
    }

    private fun extractRemoteRepositoryName(url: String): RemoteRepository.Name {
        val indexOfFourthSlash = url.indexOf(4, '/')
        val username = url.substring(url.indexOf(3, '/') + 1, indexOfFourthSlash)
        val repository = url.substring(indexOfFourthSlash + 1)
        return RemoteRepository.Name(username, repository)
    }

    override fun isCollaborator(username: String, repositoryName: RemoteRepository.Name): Boolean? {
        val response = sendRequest(URI.create(getIsCollaboratorUrl(username, repositoryName)))
        val statusCode = response.statusCode()
        return if (statusCode != 404 && statusCode != 204) {
            null
        } else statusCode == 204
    }

    private fun getIsCollaboratorUrl(username: String, repositoryName: RemoteRepository.Name) =
        "$url/repos/${repositoryName.getFullName()}/collaborators/$username"

    override fun getLicense(key: String): License? {
        val response = getJsonAt(URI.create(getLicenseUrl(key)))
        return if (response.statusCode() in 200 until 300) {
            GitHubLicense.ADAPTER.fromJson(response.body())
        } else null
    }

    private fun getLicenseUrl(key: String) = "$url/licenses/$key"

    override fun getLabels(repositoryName: RemoteRepository.Name): Array<Label>? {
        val response = getJsonAt(URI.create(getLabelsUrl(repositoryName)))
        return if (response.statusCode() in 200 until 300) {
            MOSHI.adapter<List<GitHubLabel>>(Types.newParameterizedType(List::class.java, GitHubLabel::class.java))
                .fromJson(response.body())?.toTypedArray()
        } else null
    }

    private fun getLabelsUrl(repositoryName: RemoteRepository.Name) =
        "$url/repos/${repositoryName.getFullName()}/labels"

    override fun deleteLabel(repositoryName: RemoteRepository.Name, issueNumber: Int, name: String):
            CompletableFuture<Void> {
        return CLIENT.sendAsync(
            HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(getLabelUrl(repositoryName, issueNumber, name)))
                .header(HttpHeader.ACCEPT, acceptHeader)
                .header(HttpHeader.AUTHORIZATION, "token ${GitHubAccount.getForActiveSession()?.accessToken}")
                .build(),
            HttpResponse.BodyHandlers.discarding()
        ).thenAccept {
            if (it.statusCode() != 200) {
                throw HttpRequestException("Deleting label", it.statusCode(), it.body())
            }
        }
    }

    private fun getLabelUrl(repositoryName: RemoteRepository.Name, issueNumber: Int, name: String) =
        "$url/repos/${repositoryName.getFullName()}/issues/$issueNumber/labels/${name.replace(" ", "%20")}"

    override fun getIssue(repositoryName: RemoteRepository.Name, number: Int): Issue? {
        val response = getJsonAt(URI.create(getIssueUrl(repositoryName, number)))
        return if (response.statusCode() in 200 until 300) {
            GitHubIssue.ADAPTER.fromJson(response.body())
        } else null
    }

    private fun getIssueUrl(repositoryName: RemoteRepository.Name, number: Int) =
        "$url/repos/${repositoryName.ownerName}/${repositoryName.repositoryName}/issues/$number"

    override fun getIssueTimeline(repositoryName: RemoteRepository.Name, number: Int, page: Int): Timeline? {
        val response = getJsonAt(
            URIBuilder().append(getTimelineUrl(repositoryName, number))
                .parameter("per_page", "100")
                .parameter("page", page.toString())
                .build()
        )
        return if (response.statusCode() in 200 until 300) {
            GitHubTimelineAdapter.fromJson(response.body())
        } else null
    }

    private fun getTimelineUrl(repositoryName: RemoteRepository.Name, number: Int) =
        "$url/repos/${repositoryName.ownerName}/${repositoryName.repositoryName}/issues/$number/timeline"

    override fun postIssueComment(
        repositoryName: RemoteRepository.Name,
        issueNumber: Int,
        body: String
    ): CompletableFuture<Comment> {
        return CLIENT.sendAsync(
            HttpRequest.newBuilder()
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        mapOf("body" to body).toJson()
                    )
                )
                .uri(URI.create(getCommentsUrl(repositoryName, issueNumber)))
                .header(HttpHeader.ACCEPT, acceptHeader)
                // TODO: Disallow commenting if not linked to GitHub or not logged in.
                .header(HttpHeader.AUTHORIZATION, "token ${GitHubAccount.getForActiveSession()?.accessToken}")
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).thenApply {
            it.mapOrElseThrow({ body ->
                GitHubComment.ADAPTER.fromJson(body)
            }, { "Posting GitHub issue comment." })
        }
    }

    private fun getCommentsUrl(repositoryName: RemoteRepository.Name, number: Int) =
        "$url/repos/${repositoryName.ownerName}/${repositoryName.repositoryName}/issues/$number/comments"

    override fun editIssueComment(
        repositoryName: RemoteRepository.Name,
        commentId: Int,
        body: String
    ): CompletableFuture<Comment> {
        return CLIENT.sendAsync(
            HttpRequest.newBuilder()
                .PATCH(
                    HttpRequest.BodyPublishers.ofString(
                        mapOf("body" to body).toJson()
                    )
                )
                .uri(URI.create(getCommentUrl(repositoryName, commentId)))
                .header(HttpHeader.ACCEPT, acceptHeader)
                .header(HttpHeader.AUTHORIZATION, "token ${GitHubAccount.getForActiveSession()?.accessToken}")
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).thenApply {
            it.mapOrElseThrow({ body ->
                GitHubComment.ADAPTER.fromJson(body)
            }, { "Editing GitHub issue comment." })
        }
    }

    override fun deleteIssueComment(
        repositoryName: RemoteRepository.Name,
        commentId: Int
    ): CompletableFuture<Boolean> {
        return CLIENT.sendAsync(
            HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(getCommentUrl(repositoryName, commentId)))
                .header(HttpHeader.ACCEPT, acceptHeader)
                .header(HttpHeader.AUTHORIZATION, "token ${GitHubAccount.getForActiveSession()?.accessToken}")
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).thenApply {
            it.statusCode() == 204
        }
    }

    private fun getCommentUrl(repositoryName: RemoteRepository.Name, commentId: Int) =
        "$url/repos/${repositoryName.ownerName}/${repositoryName.repositoryName}/issues/comments/$commentId"

    override fun editIssueBody(
        repositoryName: RemoteRepository.Name,
        number: Int,
        body: String
    ): CompletableFuture<Issue> {
        return createUpdateIssueRequest(
            repositoryName,
            number,
            mapOf("body" to body)
        )
    }

    override fun closeIssue(repositoryName: RemoteRepository.Name, number: Int): CompletableFuture<Issue> {
        return createUpdateIssueRequest(
            repositoryName,
            number,
            mapOf("state" to "closed")
        )
    }

    override fun openIssue(repositoryName: RemoteRepository.Name, number: Int): CompletableFuture<Issue> {
        return createUpdateIssueRequest(
            repositoryName,
            number,
            mapOf("state" to "open")
        )
    }

    private fun createUpdateIssueRequest(
        repositoryName: RemoteRepository.Name,
        number: Int,
        parameters: Map<String, String>
    ): CompletableFuture<Issue> {
        return CLIENT.sendAsync(
            HttpRequest.newBuilder()
                .PATCH(HttpRequest.BodyPublishers.ofString(parameters.toJson()))
                .uri(URI.create(getUpdateIssueUrl(repositoryName, number)))
                .header(HttpHeader.ACCEPT, acceptHeader)
                .header(HttpHeader.AUTHORIZATION, "token ${GitHubAccount.getForActiveSession()?.accessToken}")
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).thenApply {
            it.mapOrElseThrow({ body ->
                GitHubIssue.ADAPTER.fromJson(body)
            }, { "Updating GitHub issue with parameters: $parameters" })
        }
    }

    private fun getUpdateIssueUrl(repositoryName: RemoteRepository.Name, number: Int) =
        "$url/repos/${repositoryName.ownerName}/${repositoryName.repositoryName}/issues/$number"

    private fun sendRequest(uri: URI): HttpResponse<Void> {
        Session.getOrLoad()?.getGitHubAccount()?.let {
            return sendRequest(uri, "token ${it.accessToken}")
        }
        return com.harleyoconnor.gitdesk.util.network.sendRequest(uri)
    }

    private fun getJsonAt(uri: URI): HttpResponse<String> {
        Session.getOrLoad()?.getGitHubAccount()?.let {
            return getJsonAt(uri, "token ${it.accessToken}")
        }
        return com.harleyoconnor.gitdesk.util.network.getJsonAt(uri)
    }

    fun registerTypes() {
        Remote.registerType(repositoryUrlPattern) { url ->
            getRemoteRepositoryReference(url)
        }
    }
}