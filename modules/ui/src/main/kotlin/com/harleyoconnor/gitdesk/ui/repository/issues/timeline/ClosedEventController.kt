package com.harleyoconnor.gitdesk.ui.repository.issues.timeline

import com.harleyoconnor.gitdesk.data.remote.timeline.Event
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
class ClosedEventController : ViewController<ClosedEventController.Context> {

    object Loader : ResourceViewLoader<Context, ClosedEventController, HBox>(
        UIResource("/ui/layouts/repository/issues/timeline/ClosedEvent.fxml")
    )

    class Context(val event: Event) : ViewController.Context

    @FXML
    private lateinit var actorAvatar: Circle
    @FXML
    private lateinit var usernameLabel: Label
    @FXML
    private lateinit var createdLabel: Label

    override fun setup(context: Context) {
        val event = context.event
        actorAvatar.fill = ImagePattern(Image(event.actor.avatarUrl.toExternalForm()))
        usernameLabel.text = event.actor.username
        createdLabel.text = TRANSLATIONS_BUNDLE.getString("ui.date.on", event.createdAt.formatByDate())
    }
}