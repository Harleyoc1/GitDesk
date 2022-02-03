package com.harleyoconnor.gitdesk.data.remote

import com.harleyoconnor.gitdesk.data.remote.timeline.Timeline
import java.net.URL
import java.util.concurrent.CompletableFuture

/**
 * Gets platform-specific, Git-related data from remote APIs.
 */
interface PlatformNetworking {

    fun canConnect(): Boolean

    fun getUser(username: String): User?

    fun getRemoteRepository(username: String, repository: String): RemoteRepository?

    fun getRemoteRepository(url: URL): RemoteRepository?

    fun getRemoteRepositoryReference(url: URL): RemoteRepositoryReference?

    fun isCollaborator(username: String, repositoryName: RemoteRepository.Name): Boolean?

    fun getLicense(key: String): License?

    fun getLabels(repositoryName: RemoteRepository.Name): Array<Label>?

    fun addLabel(repositoryName: RemoteRepository.Name, issueNumber: Int, name: String): CompletableFuture<Void>

    fun deleteLabel(repositoryName: RemoteRepository.Name, issueNumber: Int, name: String): CompletableFuture<Void>

    /**
     * Sends a request to return all available assignees for a given repository.
     *
     * @return a future that completes when the request has been completed, either exceptionally or containing the array
     * of assignee users
     */
    fun getAssignees(repositoryName: RemoteRepository.Name, page: Int): CompletableFuture<Array<User>>

    fun addAssignee(repositoryName: RemoteRepository.Name, issueNumber: Int, username: String): CompletableFuture<Issue>

    fun getIssue(repositoryName: RemoteRepository.Name, number: Int): Issue?

    fun addIssue(repositoryName: RemoteRepository.Name, title: String, body: String): CompletableFuture<Issue>

    fun getIssueTimeline(repositoryName: RemoteRepository.Name, number: Int, page: Int): Timeline?

    /**
     * Sends a request to post a new comment on an issue.
     *
     * @param repositoryName the name of the repository containing the issue
     * @param issueNumber the issue's number
     * @param body the body of the comment to post
     * @return a future that completes when the request has been completed, either exceptionally or containing a
     * [Comment] object for the comment posted
     */
    fun addIssueComment(repositoryName: RemoteRepository.Name, issueNumber: Int, body: String):
            CompletableFuture<Comment>

    fun editIssueComment(repositoryName: RemoteRepository.Name, commentId: Int, body: String):
            CompletableFuture<Comment>

    fun deleteIssueComment(repositoryName: RemoteRepository.Name, commentId: Int): CompletableFuture<Boolean>

    /**
     * Sends a request to update the issue body.
     *
     * @param repositoryName the name of the repository containing the issue
     * @param number the issue's number
     * @param body the new body to set
     * @return a future that completes when the request has been completed, either exceptionally or containing the
     * updated [Issue] object
     */
    fun editIssueBody(repositoryName: RemoteRepository.Name, number: Int, body: String): CompletableFuture<Issue>

    /**
     * Sends a request to close an issue.
     *
     * @param repositoryName the name of the repository containing the issue
     * @param number the issue's number
     * @return a future that completes when the request has been completed, either exceptionally or containing the
     * updated [Issue] object
     */
    fun closeIssue(repositoryName: RemoteRepository.Name, number: Int): CompletableFuture<Issue>

    /**
     * Sends a request to open an issue.
     *
     * @param repositoryName the name of the repository containing the issue
     * @param number the issue's number
     * @return a future that completes when the request has been completed, either exceptionally or containing the
     * updated [Issue] object
     */
    fun openIssue(repositoryName: RemoteRepository.Name, number: Int): CompletableFuture<Issue>

}