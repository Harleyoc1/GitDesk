package com.harleyoconnor.gitdesk.data.remote

import com.harleyoconnor.gitdesk.data.remote.timeline.Timeline
import java.net.URL

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

    fun getIssue(repositoryName: RemoteRepository.Name, number: Int): Issue?

    fun getIssueTimeline(repositoryName: RemoteRepository.Name, number: Int, page: Int): Timeline?

}