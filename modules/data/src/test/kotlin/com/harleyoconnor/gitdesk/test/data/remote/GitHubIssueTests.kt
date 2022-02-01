package com.harleyoconnor.gitdesk.test.data.remote

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.github.GitHubNetworking
import com.harleyoconnor.gitdesk.util.network.CLIENT
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf

class GitHubIssueTests {

    private val testRemote by lazy {
        GitHubNetworking.getRemoteRepository("Harleyoc1", "GitDesk-Test")
    }

    @EnabledIf("com.harleyoconnor.gitdesk.test.data.remote.GitHubIssueTests#canConnectToNetwork")
    @Test
    fun `test getting issue data from GitHub`() {
        val issue = GitHubNetworking.getIssue(testRemote!!.name, 1)
        assert(issue != null)
        assert(issue?.title == "Issue title")
    }

    @EnabledIf("com.harleyoconnor.gitdesk.test.data.remote.GitHubIssueTests#canConnectToNetwork")
    @Test
    fun `test getting issue timeline data from GitHub`() {
        val timeline = GitHubNetworking.getIssueTimeline(testRemote!!.name, 1, 1)
        assert(timeline != null)
        println(timeline)
    }

    @Test
    fun `test posting issue offline`() {
        println("test")
//        try {
            GitHubNetworking.postIssueComment(
                RemoteRepository.Name("test", "test"), 1, "body"
            ).exceptionally {
                println("test")
                println("Exception: $it")
                it.printStackTrace()
                null
            }.thenAcceptAsync {
                println("test")
            }
//        } catch (t: Throwable) {
//            println("Throwable. $t")
//        }
    }

    fun canConnectToNetwork(): Boolean {
        return GitHubNetworking.canConnect()
    }

}