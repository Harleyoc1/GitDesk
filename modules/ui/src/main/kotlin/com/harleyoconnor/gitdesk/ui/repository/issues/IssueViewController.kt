package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Comment
import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.User
import com.harleyoconnor.gitdesk.data.remote.timeline.Event
import com.harleyoconnor.gitdesk.ui.Application
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.repository.LabelController
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.style.CLOSED_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.style.OPEN_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
import com.harleyoconnor.gitdesk.ui.util.CLOSED_ICON
import com.harleyoconnor.gitdesk.ui.util.OPEN_ICON
import com.harleyoconnor.gitdesk.ui.util.createErrorDialogue
import com.harleyoconnor.gitdesk.ui.util.supplyInBackground
import com.harleyoconnor.gitdesk.ui.util.thenAcceptOnMainThread
import com.harleyoconnor.gitdesk.ui.util.exceptionallyOnMainThread
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
import org.apache.logging.log4j.LogManager
import java.text.SimpleDateFormat
import java.util.Date

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

    class Context(val remoteContext: RemoteContext, val issue: Issue) : ViewController.Context

    private lateinit var remoteContext: RemoteContext
    private lateinit var issue: IssueHolder

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

    /** Stores the size of the last timeline page loaded. */
    private var lastTimelinePageSize = 0

    @FXML
    private lateinit var commentField: TextArea

    @FXML
    private lateinit var commentAndToggleStateButton: Button

    @FXML
    private lateinit var commentButton: Button

    private val toolbarView by lazy {
        IssueToolbarController.Loader.load(IssueToolbarController.Context(this, remoteContext, issue))
    }

    @FXML
    private fun initialize() {
        timelineScrollPane.whenScrolledToBottom {
            loadNextTimelinePage()
        }
    }

    override fun setup(context: Context) {
        this.remoteContext = context.remoteContext
        this.issue = IssueHolder(context.issue)
        loadAll()
    }

    override fun issueUpdated(issue: Issue) {
        this.issue.set(issue)
        loadStateLabel()
        toolbarView.controller.reloadUI()
    }

    private fun loadAll() {
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
            0, toolbarView.root
        )
    }

    private fun loadTitle() {
        titleLabel.text = issue.get().title
        numberLabel.text = "#${issue.get().number}"
    }

    private fun loadAssignees() {
        issue.get().assignees.firstOrNull()?.let {
            assigneeAvatar.fill = ImagePattern(Image(it.avatarUrl.toExternalForm()))
        }
    }

    private fun loadStateLabel() {
        if (issue.get().state == Issue.State.OPEN) {
            loadStateBoxForOpenIssue()
        } else {
            loadStateBoxForClosedIssue()
        }
        stateLabel.text = TRANSLATIONS_BUNDLE.getString("issue.state." + issue.get().state.toString().lowercase())
    }

    private fun loadStateBoxForOpenIssue() {
        stateBox.pseudoClassStateChanged(OPEN_PSEUDO_CLASS, true)
        stateBox.pseudoClassStateChanged(CLOSED_PSEUDO_CLASS, false)
        stateIcon.setupFromSvg(OPEN_ICON)
    }

    private fun loadStateBoxForClosedIssue() {
        stateBox.pseudoClassStateChanged(CLOSED_PSEUDO_CLASS, true)
        stateBox.pseudoClassStateChanged(OPEN_PSEUDO_CLASS, false)
        stateIcon.setupFromSvg(CLOSED_ICON)
    }

    private fun loadSubHeading() {
        subHeadingLabel.text = TRANSLATIONS_BUNDLE.getString(
            "ui.repository.tab.issues.view.subheading",
            issue.get().author.username,
            CREATED_AT_FORMAT.format(issue.get().createdAt),
            issue.get().comments.toString()
        )
    }

    private fun loadLabels() {
        issue.get().labels.forEach {
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

    private fun getIssueViewForRootComment(): ViewLoader.View<out CommentController, VBox> {
        val issue = this.issue.get()
        return loadCommentView(
            this,
            this.remoteContext,
            this.issue,
            Comment.Raw(null, issue.body, issue.author, issue.createdAt, issue.updatedAt)
        )
    }

    private fun loadNextTimelinePage() {
        supplyInBackground {
            loadTimelineViews().map { it.root }
        }
            .thenAcceptOnMainThread {
                timelineBox.children.addAll(it)
                loadNextTimelinePageIfLastFull()
            }
            .exceptionallyOnMainThread {
                createErrorDialogue(TRANSLATIONS_BUNDLE.getString("dialogue.error.getting_timeline"), it).show()
                LogManager.getLogger().error("Getting and compiling timeline.", it)
            }
    }

    /**
     * Loads the next timeline page if last page was full (returned 100 events).
     * This allows all timeline events to be loaded.
     */
    private fun loadNextTimelinePageIfLastFull() {
        if (lastTimelinePageSize == 100) {
            loadNextTimelinePage()
        }
    }

    private fun loadTimelineViews(): MutableList<ViewLoader.View<*, out Node>> {
        val nodes = mutableListOf<ViewLoader.View<*, out Node>>()
        lastTimelinePageSize = 0
        issue.get().getTimeline(latestTimelinePageShown++)?.forEachEvent {
            lastTimelinePageSize++
            getViewForEvent(EventContext(this, remoteContext, issue, it))?.let { node -> nodes.add(node) }
        }
        return nodes
    }

    fun toggleState() {
        getStateToggleFuture(issue.get())
            .thenAcceptOnMainThread { issue ->
                updateUIForToggledState(issue)
            }
            .exceptionallyOnMainThread {
                createErrorDialogue(TRANSLATIONS_BUNDLE.getString("dialogue.error.toggle_issue_state"), it).show()
                LogManager.getLogger().error("Could not toggle issue state.", it)
            }
    }

    private fun updateUIForToggledState(issue: Issue) {
        val createdAt = Date()
        issueUpdated(issue)
        addEventToTimeline(
            createStateChangeEvent(issue.state, remoteContext.loggedInUser!!, createdAt)
        )
    }

    private fun getStateToggleFuture(issue: Issue) =
        if (issue.state == Issue.State.OPEN)
            issue.close()
        else issue.open()

    private fun createStateChangeEvent(newState: Issue.State, actor: User, createdAt: Date): Event {
        return Event.Raw(if (newState == Issue.State.OPEN) "reopened" else "closed", actor, createdAt)
    }

    private fun addEventToTimeline(event: Event) {
        getViewForEvent(EventContext(this, remoteContext, issue, event))?.root?.let {
            timelineBox.children.add(it)
        }
    }

    @FXML
    private fun commentAndToggleState(event: ActionEvent) {

    }

    @FXML
    private fun comment(event: ActionEvent) {
        val body = commentField.text
        if (body.isNotEmpty()) {
            issue.get().addComment(body)
                .thenAcceptAsync({ comment ->
                    addCommentToTimeline(comment)
                    commentField.text = ""
                }, Application.getInstance().mainThreadExecutor)
                .exceptionallyAsync({
                    createErrorDialogue(TRANSLATIONS_BUNDLE.getString("dialogue.error.posting_issue_comment"), it)
                        .show()
                    LogManager.getLogger().error("Could not post issue comment.", it)
                    null
                }, Application.getInstance().mainThreadExecutor)
        }
    }

    private fun addCommentToTimeline(comment: Comment) {
        timelineBox.children.add(
            loadCommentView(this, this.remoteContext, this.issue, comment).root
        )
    }

}