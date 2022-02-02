package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Label
import com.harleyoconnor.gitdesk.data.remote.timeline.EventType
import com.harleyoconnor.gitdesk.data.remote.timeline.LabeledEvent
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.util.createErrorDialogue
import com.harleyoconnor.gitdesk.ui.util.exceptionallyOnMainThread
import com.harleyoconnor.gitdesk.ui.util.thenAcceptOnMainThread
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.toHexColourString
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Tooltip
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import org.apache.logging.log4j.LogManager
import java.util.Date

/**
 * @author Harley O'Connor
 */
class RemovableLabelController : ViewController<RemovableLabelController.Context> {

    object Loader : ResourceViewLoader<Context, RemovableLabelController, HBox>(
        UIResource("/ui/layouts/repository/issues/RemovableLabel.fxml")
    )

    class Context(
        val parent: TimelineController,
        val remoteContext: RemoteContext,
        val issue: IssueAccessor,
        val label: Label
    ) : ViewController.Context

    private lateinit var parent: TimelineController
    private lateinit var remoteContext: RemoteContext
    private lateinit var issue: IssueAccessor
    private lateinit var label: Label

    @FXML
    private lateinit var root: Pane

    @FXML
    private lateinit var nameLabel: javafx.scene.control.Label

    override fun setup(context: Context) {
        parent = context.parent
        remoteContext = context.remoteContext
        issue = context.issue
        label = context.label
        nameLabel.text = context.label.name
        label.description?.let {
            nameLabel.tooltip = Tooltip(it)
        }
        root.style += "-fx-border-color: ${context.label.colour.toHexColourString()};"
    }

    @FXML
    private fun remove(event: ActionEvent) {
        issue.get().deleteLabel(label.name)
            .thenAcceptOnMainThread {
                val createdAt = Date()
                parent.issueUpdated(it)
                parent.addLabeledEventToTimeline(
                    LabeledEvent.Raw(
                        EventType.UNLABELED,
                        remoteContext.loggedInUser!!,
                        createdAt,
                        label
                    )
                )
            }
            .exceptionallyOnMainThread {
                createErrorDialogue(TRANSLATIONS_BUNDLE.getString("dialogue.error.deleting_issue_label"), it).show()
                LogManager.getLogger().error("Deleting issue label.", it)
            }
    }
}