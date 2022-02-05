package com.harleyoconnor.gitdesk.ui.repository.issues.timeline

import com.harleyoconnor.gitdesk.data.remote.timeline.AssignedEvent
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
import com.harleyoconnor.gitdesk.ui.util.formatByDate
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.HBox
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Circle

/**
 * @author Harley O'Connor
 */
class AssignedEventController : ViewController<AssignedEventController.Context> {

    object Loader : ResourceViewLoader<Context, AssignedEventController, HBox>(
        UIResource("/ui/layouts/repository/issues/timeline/AssignedEvent.fxml")
    )

    class Context(val event: AssignedEvent) : ViewController.Context

    @FXML
    private lateinit var root: HBox

    @FXML
    private lateinit var actorAvatar: Circle

    @FXML
    private lateinit var actorUsernameLabel: Label

    @FXML
    private lateinit var createdLabel: Label

    @FXML
    private lateinit var eventLabel: Label

    @FXML
    private lateinit var assigneeAvatar: Circle

    @FXML
    private lateinit var assigneeUsernameLabel: Label

    override fun setup(context: Context) {
        val event = context.event
        actorAvatar.fill = ImagePattern(Image(event.actor.avatarUrl.toExternalForm()))
        actorUsernameLabel.text = event.actor.username
        if (event.actor.username == event.assignee.username) {
            eventLabel.text = TRANSLATIONS_BUNDLE.getString("issue.event.self_${event.type.toString().lowercase()}")
            root.children.removeAll(assigneeAvatar, assigneeUsernameLabel)
        } else {
            eventLabel.text = TRANSLATIONS_BUNDLE.getString("issue.event.${event.type.toString().lowercase()}")
            assigneeAvatar.fill = ImagePattern(Image(event.assignee.avatarUrl.toExternalForm()))
            assigneeUsernameLabel.text = event.assignee.username
        }
        createdLabel.text = TRANSLATIONS_BUNDLE.getString("ui.date.on", event.createdAt.formatByDate())
    }
}