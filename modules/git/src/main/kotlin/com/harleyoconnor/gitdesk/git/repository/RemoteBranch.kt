package com.harleyoconnor.gitdesk.git.repository

/**
 * @author Harley O'Connor
 */
data class RemoteBranch (
    val remote: RemoteReference,
    val name: String
)