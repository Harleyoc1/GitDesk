package com.harleyoconnor.gitdesk.ui.account.link

import com.harleyoconnor.gitdesk.data.account.Account
import com.harleyoconnor.gitdesk.data.account.GitHubAccount
import com.harleyoconnor.gitdesk.data.account.GitHubLinkingData
import com.harleyoconnor.gitdesk.data.account.Session
import com.harleyoconnor.gitdesk.data.account.getGitHubAccountRequest
import com.harleyoconnor.gitdesk.ui.util.Tab
import javafx.scene.Node
import javafx.scene.layout.Region

/**
 * @author Harley O'Connor
 */
class GitHubLinkTab(
    private val account: Account,
    setter: (Node) -> Unit
) : Tab(Region(), setter) {

    private val linkView by lazy {
        LinkGitHubController.Loader.load(LinkGitHubController.Context(this, account))
    }

    init {
        node = getGitHubAccountRequest(Session.getOrLoad()!!).join()
            .applyOrElse(
                { loadLinkedView(it).root },
                linkView.root
            )
    }

    fun toLinkedView(account: GitHubAccount) {
        node = loadLinkedView(account).root
    }

    fun toUnlinkedView() {
        node = linkView.root
    }

    fun toFinaliseLinkView(linkingData: GitHubLinkingData) {
        node = FinaliseGitHubLinkController.Loader.load(
            FinaliseGitHubLinkController.Context(this, account, linkingData)
        ).root
    }

    private fun loadLinkedView(account: GitHubAccount) =
        LinkedGitHubController.Loader.load(LinkedGitHubController.Context(this, account))

}