package com.harleyoconnor.gitdesk.ui.repository.issues.timeline

import com.harleyoconnor.gitdesk.data.remote.timeline.CommittedEvent
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox

/**
 * @author Harley O'Connor
 */
class CommittedEventController : ViewController<CommittedEventController.Context> {

    object Loader : ResourceViewLoader<Context, CommittedEventController, HBox>(
        UIResource("/ui/layouts/repository/issues/timeline/CommittedEvent.fxml")
    )

    class Context(val event: CommittedEvent) : ViewController.Context

    private lateinit var event: CommittedEvent

    @FXML
    private lateinit var committerUsernameLabel: Label

    @FXML
    private lateinit var messageLabel: Label

    @FXML
    private lateinit var hashLabel: Label

    override fun setup(context: Context) {
        event = context.event
        committerUsernameLabel.text = event.committer.name
        committerUsernameLabel.tooltip = Tooltip(event.committer.name + "\n" + event.committer.email)
        messageLabel.text = event.message
        messageLabel.tooltip = Tooltip(event.message)
        hashLabel.text = event.hash.substring(0, 8)
        hashLabel.tooltip = Tooltip(event.hash)
    }

    @FXML
    private fun commitClicked(event: MouseEvent) {
        if (event.button == MouseButton.PRIMARY) {
            Application.getInstance().hostServices.showDocument(this.event.url.toExternalForm())
        }
    }
}