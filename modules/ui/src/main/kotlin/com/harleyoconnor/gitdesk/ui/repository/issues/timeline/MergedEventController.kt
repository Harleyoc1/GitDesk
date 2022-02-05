package com.harleyoconnor.gitdesk.ui.repository.issues.timeline

import com.harleyoconnor.gitdesk.data.remote.PullRequest
import com.harleyoconnor.gitdesk.data.remote.timeline.MergedEvent
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.repository.issues.IssueAccessor
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
import com.harleyoconnor.gitdesk.ui.util.formatByDate
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Circle

/**
 * @author Harley O'Connor
 */
class MergedEventController : ViewController<MergedEventController.Context> {

    object Loader : ResourceViewLoader<Context, MergedEventController, HBox>(
        UIResource("/ui/layouts/repository/issues/timeline/MergedEvent.fxml")
    )

    class Context(
        val remoteContext: RemoteContext,
        val pullRequest: IssueAccessor<PullRequest>,
        val event: MergedEvent
    ) : ViewController.Context

    private lateinit var remoteContext: RemoteContext
    private lateinit var pullRequest: IssueAccessor<PullRequest>
    private lateinit var event: MergedEvent

    @FXML
    private lateinit var actorAvatar: Circle

    @FXML
    private lateinit var actorUsernameLabel: Label

    @FXML
    private lateinit var commitIdLabel: Label

    @FXML
    private lateinit var labelLabel: Label

    @FXML
    private lateinit var dateCreatedLabel: Label

    override fun setup(context: Context) {
        remoteContext = context.remoteContext
        pullRequest = context.pullRequest
        event = context.event
        actorAvatar.fill = ImagePattern(Image(event.actor.avatarUrl.toExternalForm()))
        actorUsernameLabel.text = event.actor.username
        commitIdLabel.text = event.commitId.substring(0, 8)
        commitIdLabel.tooltip = Tooltip(event.commitId)
        labelLabel.text = pullRequest.get().base!!.label
        dateCreatedLabel.text = TRANSLATIONS_BUNDLE.getString(
            "pull_request.event.merged.on", event.createdAt.formatByDate()
        )
    }

    @FXML
    private fun commitClicked(event: MouseEvent) {
        if (event.button == MouseButton.PRIMARY) {
            Application.getInstance().hostServices.showDocument(
                this.event.getUrl(remoteContext.remote.name).toExternalForm()
            )
        }
    }

    @FXML
    private fun baseClicked(event: MouseEvent) {
        if (event.button == MouseButton.PRIMARY) {
            val base = pullRequest.get().base!!
            Application.getInstance().hostServices.showDocument(
                base.repository.getBranchUrl(base.ref).toExternalForm()
            )
        }
    }
}