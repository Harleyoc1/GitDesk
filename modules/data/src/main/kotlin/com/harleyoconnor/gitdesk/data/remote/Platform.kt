package com.harleyoconnor.gitdesk.data.remote

import com.harleyoconnor.gitdesk.data.remote.github.GitHubNetworking
import java.net.URL
import java.util.regex.Pattern

/**
 * @author Harley O'Connor
 */
enum class Platform(
    val networking: PlatformNetworking?,
    urlRegex: String
) {
    GITHUB(GitHubNetworking, "http(s?)://(.*@)?(www\\.)?github\\.com/?.*"),
    WEB(null, "http(s?)://(.*@)?(www\\.)?[a-z0-9\\-\\.]+\\.[a-z]+/?.*");

    private val urlRegex: Pattern = Pattern.compile(urlRegex)

    /**
     * @return `true` if the specified [url] is one for this platform.
     */
    fun matchesPlatform(url: URL): Boolean {
        return urlRegex.matcher(url.toString()).matches()
    }

    companion object {
        private val PLATFORMS: Array<Platform> = arrayOf(GITHUB)

        fun getForUrl(url: URL): Platform {
            for (platform in PLATFORMS) {
                if (platform.matchesPlatform(url)) {
                    return platform
                }
            }
            return WEB
        }
    }
}