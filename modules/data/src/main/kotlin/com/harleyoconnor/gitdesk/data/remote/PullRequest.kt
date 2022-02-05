package com.harleyoconnor.gitdesk.data.remote

import java.util.Date
import java.util.concurrent.CompletableFuture

/**
 *
 * @author Harley O'Connor
 */
interface PullRequest : Issue {

    val head: RemoteBranch?

    val base: RemoteBranch?

    val draft: Boolean

    val mergeable: Boolean?

    val rebaseable: Boolean?

    val merged: Boolean

    val mergedBy: User?

    val mergedAt: Date?

    fun merge(mergedBy: User): CompletableFuture<MergeResponse>

    interface MergeResponse {
        val commitId: String
    }

}