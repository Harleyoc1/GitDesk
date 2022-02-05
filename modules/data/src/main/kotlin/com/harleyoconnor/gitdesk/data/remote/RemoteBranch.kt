package com.harleyoconnor.gitdesk.data.remote

/**
 *
 * @author Harley O'Connor
 */
interface RemoteBranch {

    val label: String

    val ref: String

    val user: User

    val repository: RemoteRepository

}