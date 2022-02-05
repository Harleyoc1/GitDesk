package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewLoader
import javafx.scene.Node
import javafx.scene.layout.VBox

/**
 * @author Harley O'Connor
 */
class IssueViewController : AbstractIssueViewController<Issue, IssueViewController.Context>() {

    object Loader : ResourceViewLoader<Context, IssueViewController, VBox>(
        UIResource("/ui/layouts/repository/issues/IssueView.fxml")
    )

    class Context(remoteContext: RemoteContext, issue: Issue, refreshCallback: (Int) -> Unit) :
        AbstractIssueViewController.Context<Issue>(remoteContext, issue, refreshCallback)

    override val toolbarView: ViewLoader.View<IssueToolbarController, out Node> by lazy {
        IssueToolbarController.Loader.load(IssueToolbarController.Context(this, remoteContext, issue))
    }
}