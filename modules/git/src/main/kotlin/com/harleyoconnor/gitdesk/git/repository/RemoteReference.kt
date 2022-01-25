package com.harleyoconnor.gitdesk.git.repository

import com.harleyoconnor.gitdesk.util.Directory

/**
 * A locally stored reference to a [Remote], in terms of a [name] and the [remote] object.
 *
 * @author Harley O'Connor
 */
data class RemoteReference(
    val name: String,
    val remote: Remote
) {

    companion object {
        fun getForBranch(branchName: String, repoDirectory: Directory): RemoteReference? {
            val remoteName = Remote.getUpstreamName(repoDirectory, branchName) ?: return null
            val remoteUrl = Remote.getUrl(repoDirectory, remoteName) ?: return null
            return RemoteReference(remoteName, Remote.getRemote(remoteUrl))
        }
    }

}