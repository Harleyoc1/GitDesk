package com.harleyoconnor.gitdesk.data.remote.github

import com.harleyoconnor.gitdesk.data.remote.License
import com.harleyoconnor.gitdesk.data.remote.PlatformNetworking
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.User
import com.harleyoconnor.gitdesk.git.repository.Remote
import com.harleyoconnor.gitdesk.util.indexOf
import com.harleyoconnor.gitdesk.util.network.getJsonAt
import java.net.URI
import java.net.URL
import java.util.regex.Pattern

object GitHubNetworking : PlatformNetworking {

    const val url = "https://api.github.com"
    private const val acceptableUsernameRange = "A-Za-z0-9-"
    private const val acceptableRepositoryRange = "$acceptableUsernameRange\\_."

    private val repositoryUrlPattern =
        Pattern.compile("http(s?)://(.*@)?(www\\.)?github\\.com/[$acceptableUsernameRange]{4,}/[$acceptableRepositoryRange]+/?")

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
        val name = extractRepositoryName(url)
        return getRemoteRepository(name.ownerName, name.repositoryName)
    }

    private fun extractRepositoryName(url: URL): RemoteRepository.Name {
        assert(repositoryUrlPattern.matcher(url.toExternalForm()).matches()) {
            IllegalArgumentException("$url not a valid GitHub repository URL.")
        }
        return extractRepositoryName(url.toExternalForm().removeSuffix("/").removeSuffix(".git"))
    }

    private fun extractRepositoryName(url: String): RemoteRepository.Name {
        val indexOfFourthSlash = url.indexOf(4, '/')
        val username = url.substring(url.indexOf(3, '/') + 1, indexOfFourthSlash)
        val repository = url.substring(indexOfFourthSlash + 1)
        return RemoteRepository.Name(username, repository)
    }

    override fun getLicense(key: String): License? {
        val response = getJsonAt(URI.create(getLicenseUrl(key)))
        return if (response.statusCode() in 200 until 300) {
            GitHubLicense.ADAPTER.fromJson(response.body())
        } else null
    }

    private fun getLicenseUrl(key: String) = "$url/licenses/$key"

    fun registerTypes() {
        Remote.registerType(repositoryUrlPattern) { url ->
            getRemoteRepository(url)
        }
    }
}