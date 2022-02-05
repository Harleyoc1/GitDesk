package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue

interface IssueAccessor {
    fun get(): Issue
}

class IssueHolder(
    var issue: Issue
) : IssueAccessor {

    override fun get() = issue

    fun set(issue: Issue) {
        this.issue = issue
    }

}