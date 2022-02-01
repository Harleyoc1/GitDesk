package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.IssueComment
import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.repository.LabelController
import com.harleyoconnor.gitdesk.ui.style.CLOSED_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.style.OPEN_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
import com.harleyoconnor.gitdesk.ui.util.CLOSED_ICON
import com.harleyoconnor.gitdesk.ui.util.OPEN_ICON
import com.harleyoconnor.gitdesk.ui.util.whenScrolledToBottom
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.ui.view.ViewLoader
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Circle
import java.text.SimpleDateFormat
import java.util.concurrent.CompletableFuture

/**
 * @author Harley O'Connor
 */
class IssueViewController : ViewController<IssueViewController.Context>, TimelineController {

    companion object {
        private val CREATED_AT_FORMAT = SimpleDateFormat("dd MMM yyyy")
    }

    object Loader : ResourceViewLoader<Context, IssueViewController, VBox>(
        UIResource("/ui/layouts/repository/issues/IssueView.fxml")
    )

    class Context(val repository: RemoteRepository, val issue: Issue) : ViewController.Context

    private lateinit var repository: RemoteRepository
    private lateinit var issue: Issue

    @FXML
    private lateinit var root: VBox

    @FXML
    private lateinit var titleLabel: Label

    @FXML
    private lateinit var numberLabel: Label

    @FXML
    private lateinit var assigneeAvatar: Circle

    @FXML
    private lateinit var subHeadingLabel: Label

    @FXML
    private lateinit var stateBox: HBox

    @FXML
    private lateinit var stateIcon: SVGIcon

    @FXML
    private lateinit var stateLabel: Label

    @FXML
    private lateinit var labelsBox: HBox

    @FXML
    private lateinit var timelineScrollPane: ScrollPane

    @FXML
    private lateinit var timelineBox: VBox

    /** Stores the latest timeline page currently being shown. */
    private var latestTimelinePageShown = 1

    @FXML
    private lateinit var commentField: TextArea

    @FXML
    private lateinit var commentButton: Button

    @FXML
    private fun initialize() {
        timelineScrollPane.whenScrolledToBottom {
            loadNextTimelinePage()
        }
    }

    override fun setup(context: Context) {
        this.repository = context.repository
        this.issue = context.issue

        loadToolbar()
        loadTitle()
        loadAssignees()
        loadStateLabel()
        loadSubHeading()
        loadLabels()
        loadTimeline()
    }

    private fun loadToolbar() {
        root.children.add(
            0, IssueToolbarController.Loader.load(IssueToolbarController.Context(repository, issue)).root
        )
    }

    private fun loadTitle() {
        titleLabel.text = issue.title
        numberLabel.text = "#${issue.number}"
    }

    private fun loadAssignees() {
        issue.assignees.firstOrNull()?.let {
            assigneeAvatar.fill = ImagePattern(Image(it.avatarUrl.toExternalForm()))
        }
    }

    private fun loadStateLabel() {
        if (issue.state == Issue.State.OPEN) {
            setupUIForOpenIssue()
        } else if (issue.state == Issue.State.CLOSED) {
            setupUIForClosedIssue()
        }
        stateLabel.text = TRANSLATIONS_BUNDLE.getString("issue.state." + issue.state.toString().lowercase())
    }

    private fun setupUIForOpenIssue() {
        stateBox.pseudoClassStateChanged(OPEN_PSEUDO_CLASS, true)
        stateIcon.setupFromSvg(OPEN_ICON)
    }

    private fun setupUIForClosedIssue() {
        stateBox.pseudoClassStateChanged(CLOSED_PSEUDO_CLASS, true)
        stateIcon.setupFromSvg(CLOSED_ICON)
    }

    private fun loadSubHeading() {
        subHeadingLabel.text = TRANSLATIONS_BUNDLE.getString(
            "ui.repository.tab.issues.view.subheading",
            issue.author.username,
            CREATED_AT_FORMAT.format(issue.createdAt),
            issue.comments.toString()
        )
    }

    private fun loadLabels() {
        issue.labels.forEach {
            labelsBox.children.add(loadLabelView(it).root)
        }
    }

    private fun loadLabelView(label: com.harleyoconnor.gitdesk.data.remote.Label):
            ViewLoader.View<LabelController, Pane> {
        return LabelController.Loader.load(LabelController.Context(label))
    }

    override fun remove(node: Node) {
        timelineBox.children.remove(node)
    }

    private fun loadTimeline() {
        // Load root comment.
        timelineBox.children.add(
            getIssueViewForRootComment().root
        )
        // Load rest of timeline events.
        loadNextTimelinePage()
    }

    private fun loadNextTimelinePage() {
        CompletableFuture.supplyAsync({
            loadTimelineViews().map { it.root }
        }, Application.getInstance().backgroundExecutor)
            .thenAcceptAsync({
                timelineBox.children.addAll(it)
            }, Application.getInstance().mainThreadExecutor)
    }

    private fun loadTimelineViews(): MutableList<ViewLoader.View<*, out Node>> {
        val nodes = mutableListOf<ViewLoader.View<*, out Node>>()
        issue.getTimeline(repository.name, latestTimelinePageShown++)?.forEachEvent {
            getViewForEvent(this, issue, it)?.let { node -> nodes.add(node) }
        }
        return nodes
    }

    private fun getIssueViewForRootComment() = CommentController.Loader.load(
        CommentController.Context(
            issue, IssueComment(issue.body, issue.author, issue.createdAt, issue.updatedAt)
        )
    )

    @FXML
    private fun toggleState(event: ActionEvent) {

    }

    @FXML
    private fun pin(event: ActionEvent) {

    }

    @FXML
    private fun delete(event: ActionEvent) {

    }

    @FXML
    private fun commentAndClose(event: ActionEvent) {

    }

    @FXML
    private fun comment(event: ActionEvent) {

    }


}