package com.harleyoconnor.gitdesk.data.remote.github

import com.harleyoconnor.gitdesk.data.remote.RemoteBranch

/**
 *
 * @author Harley O'Connor
 */
class GitHubRemoteBranch(
    override val label: String,
    override val ref: String,
    override val user: GitHubUser,
    override val repo: GitHubRemoteRepository
) : RemoteBranch