package com.harleyoconnor.gitdesk.git.repository

/**
 * A locally stored reference to a [Remote], in terms of a [name] and the [remote] object.
 *
 * @author Harley O'Connor
 */
class RemoteReference(
    val name: String,
    val remote: Remote
)