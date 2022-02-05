package com.harleyoconnor.gitdesk.data.remote.github

import com.harleyoconnor.gitdesk.data.remote.RemoteBranch
import com.squareup.moshi.Json

/**
 *
 * @author Harley O'Connor
 */
class GitHubRemoteBranch(
    override val label: String,
    override val ref: String,
    override val user: GitHubUser,
    @Json(name = "repo") override val repository: GitHubRemoteRepository?
) : RemoteBranch