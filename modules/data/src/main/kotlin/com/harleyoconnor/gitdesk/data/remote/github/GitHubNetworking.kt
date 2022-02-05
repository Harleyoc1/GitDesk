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
import com.harleyoconnor.gitdesk.data.remote.PullRequest
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.RemoteRepositoryReference
import com.harleyoconnor.gitdesk.data.remote.User
import com.harleyoconnor.gitdesk.data.remote.github.timeline.GitHubTimelineAdapter
import com.harleyoconnor.gitdesk.data.remote.timeline.Timeline
import com.harleyoconnor.gitdesk.data.serialisation.adapter.toJson
import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.util.indexOf
import com.harleyoconnor.gitdesk.util.network.CLIENT
import com.harleyoconnor.gitdesk.util.network.DELETE
import com.harleyoconnor.gitdesk.util.network.HttpHeader
import com.harleyoconnor.gitdesk.util.network.HttpRequestException
import com.harleyoconnor.gitdesk.util.network.PATCH
import com.harleyoconnor.gitdesk.util.network.URIBuilder
import com.harleyoconnor.gitdesk.util.network.getJsonAt
import com.harleyoconnor.gitdesk.util.network.mapOrElseThrow
import com.harleyoconnor.gitdesk.util.network.sendRequest
import com.harleyoconnor.gitdesk.util.network.uri
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import java.net.URI
import java.net.URL
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture
import java.util.regex.Pattern

private const val ACCEPT_HEADER = "application/vnd.github.v3+json"

object GitHubNetworking : PlatformNetworking {

    const val url = "https://api.github.com"
    const val acceptableUsernameRange = "A-Za-z0-9-"
    const val acceptableRepositoryRange = "$acceptableUsernameRange\\_."

    private val repositoryUrlPattern =
        Pattern.compile("http(s?)://(.*@)?(www\\.)?github\\.com/[$acceptableUsernameRange]{4,}/[$acceptableRepositoryRange]+/?")

    private val userListAdapter by lazy {
        MOSHI.adapter<List<User>>(
            Types.newParameterizedType(List::class.java, GitHubUser::class.java)
        )
    }

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

    override fun addLabel(
        repositoryName: RemoteRepository.Name,
        issueNumber: Int,
        name: String
    ): CompletableFuture<Void> {
        return CLIENT.sendAsync(
            HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(Labels.ADAPTER.toJson(Labels(arrayOf(name)))))
                .uri(getLabelsUrl(repositoryName, issueNumber))
                .applyDefaultHeaders()
                .build(),
            HttpResponse.BodyHandlers.discarding()
        ).thenAccept {
            if (it.statusCode() != 200) {
                throw HttpRequestException("Adding", it.statusCode(), it.body())
            }
        }
    }

    private fun getLabelsUrl(repositoryName: RemoteRepository.Name, issueNumber: Int) =
        "$url/repos/${repositoryName.getFullName()}/issues/$issueNumber/labels"

    private class Labels(
        private val labels: Array<String>
    ) {

        companion object {
            val ADAPTER: JsonAdapter<Labels> = MOSHI.adapter(Labels::class.java)
        }
    }

    override fun deleteLabel(repositoryName: RemoteRepository.Name, issueNumber: Int, name: String):
            CompletableFuture<Void> {
        return CLIENT.sendAsync(
            HttpRequest.newBuilder()
                .DELETE()
                .uri(getLabelUrl(repositoryName, issueNumber, name))
                .applyDefaultHeaders()
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

    override fun getAssignees(repositoryName: RemoteRepository.Name, page: Int): CompletableFuture<Array<User>> {
        return CLIENT.sendAsync(
            HttpRequest.newBuilder()
                .GET()
                .uri(
                    URIBuilder()
                        .append(getAssigneesUrl(repositoryName))
                        .parameter("page", page.toString())
                        .parameter("per_page", "100")
                        .build()
                )
                .applyDefaultHeaders()
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).thenApply {
            it.mapOrElseThrow({ body ->
                userListAdapter.fromJson(body)?.toTypedArray()
            }, { "Retrieving assignees." })
        }
    }

    private fun getAssigneesUrl(repositoryName: RemoteRepository.Name) =
        "$url/repos/${repositoryName.getFullName()}/assignees"

    override fun addAssignee(
        repositoryName: RemoteRepository.Name,
        issueNumber: Int,
        username: String
    ): CompletableFuture<Issue> {
        return CLIENT.sendAsync(
            HttpRequest.newBuilder()
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        Assignees.ADAPTER.toJson(Assignees(arrayOf(username)))
                    )
                )
                .uri(getAssigneesUrl(repositoryName, issueNumber))
                .applyDefaultHeaders()
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).thenApply {
            it.mapOrElseThrow({ body ->
                GitHubIssue.ADAPTER.fromJson(body)
            }, { "Assigning issue." })
        }
    }

    override fun removeAssignee(
        repositoryName: RemoteRepository.Name,
        issueNumber: Int,
        username: String
    ): CompletableFuture<Issue> {
        return CLIENT.sendAsync(
            HttpRequest.newBuilder()
                .DELETE(
                    HttpRequest.BodyPublishers.ofString(
                        Assignees.ADAPTER.toJson(Assignees(arrayOf(username)))
                    )
                )
                .uri(getAssigneesUrl(repositoryName, issueNumber))
                .applyDefaultHeaders()
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).thenApply {
            it.mapOrElseThrow({ body ->
                GitHubIssue.ADAPTER.fromJson(body)
            }, { "Un-assigning issue." })
        }
    }

    private fun getAssigneesUrl(repositoryName: RemoteRepository.Name, issueNumber: Int) =
        "$url/repos/${repositoryName.getFullName()}/issues/$issueNumber/assignees"

    private class Assignees(
        private val assignees: Array<String>
    ) {
        companion object {
            val ADAPTER: JsonAdapter<Assignees> = MOSHI.adapter(Assignees::class.java)
        }
    }

    override fun getIssue(repositoryName: RemoteRepository.Name, number: Int): Issue? {
        val response = getJsonAt(URI.create(getIssueUrl(repositoryName, number)))
        return if (response.statusCode() in 200 until 300) {
            GitHubIssue.ADAPTER.fromJson(response.body())
        } else null
    }

    private fun getIssueUrl(repositoryName: RemoteRepository.Name, number: Int) =
        "$url/repos/${repositoryName.ownerName}/${repositoryName.repositoryName}/issues/$number"

    override fun addIssue(
        repositoryName: RemoteRepository.Name,
        title: String,
        body: String
    ): CompletableFuture<Issue> {
        return CLIENT.sendAsync(
            HttpRequest.newBuilder()
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        IssueCreationData.ADAPTER.toJson(IssueCreationData(title, body))
                    )
                )
                .uri(getIssuesUrl(repositoryName))
                .applyDefaultHeaders()
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).thenApply {
            it.mapOrElseThrow({ body ->
                GitHubIssue.ADAPTER.fromJson(body)
            }, { "Adding issue." })
        }
    }

    private class IssueCreationData(
        private val title: String,
        private val body: String
    ) {
        companion object {
            val ADAPTER: JsonAdapter<IssueCreationData> = MOSHI.adapter(IssueCreationData::class.java)
        }
    }

    private fun getIssuesUrl(repositoryName: RemoteRepository.Name) =
        "$url/repos/${repositoryName.ownerName}/${repositoryName.repositoryName}/issues"

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

    override fun addIssueComment(
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
                .uri(getCommentsUrl(repositoryName, issueNumber))
                .applyDefaultHeaders()
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
                .uri(getCommentUrl(repositoryName, commentId))
                .applyDefaultHeaders()
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
                .uri(getCommentUrl(repositoryName, commentId))
                .applyDefaultHeaders()
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
            mapOf("body" to body),
            false
        )
    }

    override fun closeIssue(repositoryName: RemoteRepository.Name, number: Int): CompletableFuture<Issue> {
        return createUpdateIssueRequest(
            repositoryName,
            number,
            mapOf("state" to "closed"),
            false
        )
    }

    override fun openIssue(repositoryName: RemoteRepository.Name, number: Int): CompletableFuture<Issue> {
        return createUpdateIssueRequest(
            repositoryName,
            number,
            mapOf("state" to "open"),
            false
        )
    }

    private fun createUpdateIssueRequest(
        repositoryName: RemoteRepository.Name,
        number: Int,
        parameters: Map<String, String>,
        pullRequest: Boolean
    ): CompletableFuture<Issue> {
        return CLIENT.sendAsync(
            HttpRequest.newBuilder()
                .PATCH(HttpRequest.BodyPublishers.ofString(parameters.toJson()))
                .uri(getUpdateIssueUrl(repositoryName, number))
                .applyDefaultHeaders()
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).thenApply {
            mapIssueOrThrow(it, pullRequest) {
                "Updating GitHub issue with parameters: $parameters"
            }
        }
    }

    private fun getUpdateIssueUrl(repositoryName: RemoteRepository.Name, number: Int) =
        "$url/repos/${repositoryName.ownerName}/${repositoryName.repositoryName}/issues/$number"

    override fun getPullRequest(repositoryName: RemoteRepository.Name, number: Int): CompletableFuture<PullRequest> {
        return CLIENT.sendAsync(
            HttpRequest.newBuilder()
                .GET()
                .uri(getPullRequestUrl(repositoryName, number))
                .applyDefaultHeaders()
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).thenApply {
            mapPullRequestOrThrow(it) {
                "Retrieving pull request."
            }
        }
    }

    private fun mapIssueOrThrow(
        response: HttpResponse<String>,
        pullRequest: Boolean,
        messageSupplier: () -> String
    ): Issue {
        return if (pullRequest)
            mapPullRequestOrThrow(response, messageSupplier)
        else mapIssueOrThrow(response, messageSupplier)
    }

    private fun mapIssueOrThrow(
        response: HttpResponse<String>,
        messageSupplier: () -> String
    ): Issue {
        return response.mapOrElseThrow({ body ->
            GitHubIssue.ADAPTER.fromJson(body)!!
        }, messageSupplier)
    }

    private fun mapPullRequestOrThrow(
        response: HttpResponse<String>,
        messageSupplier: () -> String
    ): PullRequest {
        return response.mapOrElseThrow({ body ->
            GitHubPullRequest.ADAPTER.fromJson(body)!!
        }, messageSupplier)
    }

    private fun getPullRequestUrl(repositoryName: RemoteRepository.Name, number: Int) =
        "$url/repos/${repositoryName.ownerName}/${repositoryName.repositoryName}/pulls/$number"

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

private fun HttpRequest.Builder.applyDefaultHeaders(): HttpRequest.Builder {
    this.header(HttpHeader.ACCEPT, ACCEPT_HEADER)
    GitHubAccount.getForActiveSession()?.let { account ->
        this.header(HttpHeader.AUTHORIZATION, "token ${account.accessToken}")
    }
    return this
}