package com.harleyoconnor.gitdesk.data.remote

import com.harleyoconnor.gitdesk.git.repository.Remote

fun Remote.withFullData(): RemoteRepository? {
    return if (this is RemoteRepository) {
        this
    } else if (this is RemoteRepositoryReference) {
        this.getRemoteRepository()
    } else null
}

fun RemoteRepository.Order.asGitHubId(): String {
    return if (this == RemoteRepository.Order.ASCENDING) {
        "asc"
    } else "desc"
}