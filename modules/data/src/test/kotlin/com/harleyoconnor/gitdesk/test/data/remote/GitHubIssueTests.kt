package com.harleyoconnor.gitdesk.test.data.remote

import com.harleyoconnor.gitdesk.data.remote.github.GitHubNetworking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf

class GitHubIssueTests {

    private val testRemote by lazy {
        GitHubNetworking.getRemoteRepository("Harleyoc1", "GitDesk-Test")
    }

    @Disabled("Test remote deleted")
    @EnabledIf("com.harleyoconnor.gitdesk.test.data.remote.GitHubIssueTests#canConnectToNetwork")
    @Test
    fun `test getting issue data from GitHub`() {
        val issue = GitHubNetworking.getIssue(testRemote!!.name, 1)
        assert(issue != null)
        assert(issue?.title == "Issue title")
    }

    @Disabled("Test remote deleted")
    @EnabledIf("com.harleyoconnor.gitdesk.test.data.remote.GitHubIssueTests#canConnectToNetwork")
    @Test
    fun `test getting issue timeline data from GitHub`() {
        val timeline = GitHubNetworking.getIssueTimeline(testRemote!!.name, 1, 1)
        assert(timeline != null)
        println(timeline)
    }

    fun canConnectToNetwork(): Boolean {
        return GitHubNetworking.canConnect()
    }

}