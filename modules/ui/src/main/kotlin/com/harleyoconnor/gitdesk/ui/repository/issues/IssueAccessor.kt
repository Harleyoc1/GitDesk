package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue

interface IssueAccessor<I: Issue> {
    fun get(): I
}

class IssueHolder<I: Issue>(
    var issue: I
) : IssueAccessor<I> {

    override fun get() = issue

    fun set(issue: I) {
        this.issue = issue
    }

}