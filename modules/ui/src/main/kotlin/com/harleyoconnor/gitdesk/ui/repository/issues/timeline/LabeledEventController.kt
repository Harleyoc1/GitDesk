package com.harleyoconnor.gitdesk.ui.repository.issues.timeline

import com.harleyoconnor.gitdesk.data.remote.timeline.EventType
import com.harleyoconnor.gitdesk.data.remote.timeline.LabeledEvent
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.repository.issues.LabelController
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.HBox
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Circle
import java.text.SimpleDateFormat

/**
 * @author Harley O'Connor
 */
class LabeledEventController : ViewController<LabeledEventController.Context> {

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("dd MMM yyyy")
    }

    object Loader : ResourceViewLoader<Context, LabeledEventController, HBox>(
        UIResource("/ui/layouts/repository/issues/timeline/LabeledEvent.fxml")
    )

    class Context(val label: com.harleyoconnor.gitdesk.data.remote.Label, val labeledEvent: LabeledEvent) :
        ViewController.Context

    @FXML
    private lateinit var root: HBox

    @FXML
    private lateinit var actorAvatar: Circle

    @FXML
    private lateinit var usernameLabel: Label

    @FXML
    private lateinit var eventLabel: Label

    @FXML
    private lateinit var createdLabel: Label

    override fun setup(context: Context) {
        val event = context.labeledEvent
        actorAvatar.fill = ImagePattern(Image(event.actor.avatarUrl.toExternalForm()))
        usernameLabel.text = event.actor.username
        if (event.type == EventType.UNLABELED) {
            eventLabel.text = TRANSLATIONS_BUNDLE.getString("issue.event.unlabeled")
        }
        root.children.add(4, LabelController.Loader.load(LabelController.Context(context.label)).root)
        createdLabel.text = TRANSLATIONS_BUNDLE.getString("ui.date.on", DATE_FORMAT.format(event.createdAt))
    }
}