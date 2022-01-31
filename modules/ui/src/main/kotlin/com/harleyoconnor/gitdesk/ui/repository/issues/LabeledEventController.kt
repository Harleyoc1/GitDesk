package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.timeline.LabeledEvent
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.repository.LabelController
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
        UIResource("/ui/layouts/repository/issues/LabeledEvent.fxml")
    )

    class Context(val labeledEvent: LabeledEvent) : ViewController.Context

    @FXML
    private lateinit var root: HBox
    @FXML
    private lateinit var actorAvatar: Circle
    @FXML
    private lateinit var usernameLabel: Label
    @FXML
    private lateinit var createdLabel: Label

    override fun setup(context: Context) {
        val event = context.labeledEvent
        actorAvatar.fill = ImagePattern(Image(event.actor.avatarUrl.toExternalForm()))
        usernameLabel.text = event.actor.username
        root.children.add(4, LabelController.Loader.load(LabelController.Context(event.label)).root)
        createdLabel.text = TRANSLATIONS_BUNDLE.getString("ui.date.on", DATE_FORMAT.format(event.createdAt))
    }
}