package com.harleyoconnor.gitdesk.data.serialisation.qualifier

import com.squareup.moshi.JsonQualifier

/**
 * Json qualifier for loading the URL of a repository as a
 * [RemoteRepository.Name][com.harleyoconnor.gitdesk.data.remote.RemoteRepository.Name].
 *
 * @author Harley O'Connor
 */
@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class GitHubRepositoryNameFromUrl
