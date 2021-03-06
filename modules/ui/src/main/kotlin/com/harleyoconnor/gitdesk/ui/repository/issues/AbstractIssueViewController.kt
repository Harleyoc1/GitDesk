package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Comment
import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.User
import com.harleyoconnor.gitdesk.data.remote.timeline.AssignedEvent
import com.harleyoconnor.gitdesk.data.remote.timeline.Event
import com.harleyoconnor.gitdesk.data.remote.timeline.EventType
import com.harleyoconnor.gitdesk.data.remote.timeline.LabeledEvent
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.repository.issues.timeline.CommentController
import com.harleyoconnor.gitdesk.ui.repository.issues.timeline.EventContext
import com.harleyoconnor.gitdesk.ui.repository.issues.timeline.IssueController
import com.harleyoconnor.gitdesk.ui.repository.issues.timeline.getViewForEvent
import com.harleyoconnor.gitdesk.ui.repository.issues.timeline.loadCommentView
import com.harleyoconnor.gitdesk.ui.style.CLOSED_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.style.OPEN_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.translation.getString
import com.harleyoconnor.gitdesk.ui.util.createAvatarNode
import com.harleyoconnor.gitdesk.ui.util.exceptionallyOnMainThread
import com.harleyoconnor.gitdesk.ui.util.logErrorAndCreateDialogue
import com.harleyoconnor.gitdesk.ui.util.supplyInBackground
import com.harleyoconnor.gitdesk.ui.util.thenAcceptOnMainThread
import com.harleyoconnor.gitdesk.ui.util.whenScrolledToBottom
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.ui.view.ViewLoader
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.shape.Circle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.CompletableFuture

/**
 * @author Harley O'Connor
 */
abstract class AbstractIssueViewController<I : Issue, C : AbstractIssueViewController.Context<I>> :
    ViewController<C>,
    IssueController {

    companion object {
        private val CREATED_AT_FORMAT = SimpleDateFormat("dd MMM yyyy")
    }

    open class Context<I : Issue>(val remoteContext: RemoteContext, val issue: I, val refreshCallback: (Int) -> Unit) :
        ViewController.Context

    protected lateinit var remoteContext: RemoteContext
    protected lateinit var issue: I
    private lateinit var refreshCallback: (Int) -> Unit

    @FXML
    private lateinit var root: VBox

    @FXML
    private lateinit var titleLabel: Label

    @FXML
    private lateinit var numberLabel: Label

    @FXML
    private lateinit var assigneesBox: HBox

    @FXML
    private lateinit var addAssigneeButton: Button

    @FXML
    protected lateinit var subHeadingLabel: Label

    @FXML
    protected lateinit var stateBox: HBox

    @FXML
    protected lateinit var stateIcon: SVGIcon

    @FXML
    protected lateinit var stateLabel: Label

    @FXML
    private lateinit var labelsBox: HBox

    @FXML
    private lateinit var addLabelButton: Button

    @FXML
    private lateinit var timelineScrollPane: ScrollPane

    @FXML
    private lateinit var timelineBox: VBox

    /** Stores the latest timeline page currently being shown. */
    private var latestTimelinePageShown = 1

    /** Stores the size of the last timeline page loaded. */
    private var lastTimelinePageSize = 0

    @FXML
    protected lateinit var commentBox: VBox

    @FXML
    private lateinit var commentField: TextArea

    @FXML
    protected lateinit var commentButtonsBox: HBox

    @FXML
    private lateinit var commentAndToggleStateButton: Button

    @FXML
    private lateinit var commentButton: Button

    protected abstract val toolbarView: ViewLoader.View<out AbstractIssueToolbarController<*, *>, out Node>

    @FXML
    private fun initialize() {
        timelineScrollPane.whenScrolledToBottom {
            loadNextTimelinePage()
        }
        commentField.textProperty().addListener { _, old, new ->
            if (new.isEmpty()) {
                updateForEmptyCommentField()
            } else if (old.isEmpty()) {
                updateForNonEmptyCommentField()
            }
        }
    }

    override fun setup(context: C) {
        this.remoteContext = context.remoteContext
        this.issue = context.issue
        this.refreshCallback = context.refreshCallback
        loadAll()
    }

    override fun refresh() {
        refreshCallback(issue.number)
    }

    override fun issueUpdated() {
        toolbarView.controller.reloadUI()
        loadAssignees()
        loadStateBox()
        loadSubHeading()
        loadLabels()
        if (commentField.text.isEmpty()) {
            updateForEmptyCommentField()
        } else {
            updateForNonEmptyCommentField()
        }
    }

    private fun loadAll() {
        loadToolbar()
        loadTitle()
        loadAssignees()
        loadStateBox()
        loadSubHeading()
        loadLabels()
        loadTimeline()
        loadCommentCompositionBox()
        updateForEmptyCommentField()
    }

    private fun loadToolbar() {
        root.children.add(
            0, toolbarView.root
        )
    }

    private fun loadTitle() {
        titleLabel.text = issue.title
        numberLabel.text = "#${issue.number}"
    }

    private fun loadAssignees() {
        assigneesBox.children.clear()
        val assignees = issue.assignees
        if (remoteContext.loggedInUserIsCollaborator && canAddMoreAssignees(assignees)) {
            assigneesBox.children.add(0, addAssigneeButton)
        }
        assignees.forEach {
            assigneesBox.children.add(
                createAssigneeNode(it)
            )
        }
    }

    private fun createAssigneeNode(user: User): Circle {
        return user.createAvatarNode().also {
            val contextMenu = ContextMenu(
                createRemoveAssigneeItem(user)
            )
            it.setOnContextMenuRequested { event ->
                contextMenu.show(root, event.screenX, event.screenY)
            }
        }
    }

    private fun createRemoveAssigneeItem(user: User): MenuItem {
        return MenuItem(TRANSLATIONS_BUNDLE.getString("issue.assignee.remove")).also { item ->
            item.setOnAction { removeAssignee(user) }
        }
    }

    /**
     * @return `true` if there is capacity for more assignees
     */
    private fun canAddMoreAssignees(assignees: Array<out User>) =
        assignees.size < 10

    protected open fun loadStateBox() {
        stateBox.pseudoClassStateChanged(OPEN_PSEUDO_CLASS, issue.state == Issue.State.OPEN)
        stateBox.pseudoClassStateChanged(CLOSED_PSEUDO_CLASS, issue.state == Issue.State.CLOSED)
        if (issue.state == Issue.State.OPEN) {
            loadStateBoxForOpen()
        } else {
            loadStateBoxForClosed()
        }
        stateLabel.text = TRANSLATIONS_BUNDLE.getString("issue.state." + issue.state.toString().lowercase())
    }

    protected abstract fun loadStateBoxForOpen()

    protected abstract fun loadStateBoxForClosed()

    protected open fun loadSubHeading() {
        subHeadingLabel.text = TRANSLATIONS_BUNDLE.getString(
            "ui.repository.tab.issues.view.subheading",
            issue.author.username,
            CREATED_AT_FORMAT.format(issue.createdAt),
            issue.comments.toString()
        )
    }

    private fun loadLabels() {
        labelsBox.children.clear()
        if (remoteContext.loggedInUserIsCollaborator) {
            labelsBox.children.add(addLabelButton)
        }
        issue.labels.forEach {
            labelsBox.children.add(loadLabelView(it).root)
        }
    }

    private fun loadLabelView(label: com.harleyoconnor.gitdesk.data.remote.Label):
            ViewLoader.View<*, out Pane> {
        return if (remoteContext.loggedInUserIsCollaborator) {
            RemovableLabelController.Loader.load(
                RemovableLabelController.Context(
                    this,
                    remoteContext,
                    this.issue,
                    label
                )
            )
        } else LabelController.Loader.load(LabelController.Context(label))
    }

    private fun updateForEmptyCommentField() {
        commentButton.isDisable = true
        commentAndToggleStateButton.text =
            TRANSLATIONS_BUNDLE.getString(
                "ui.button.toggle_from_${
                    issue.state.toString().lowercase()
                }"
            )
    }

    private fun updateForNonEmptyCommentField() {
        commentButton.isDisable = false
        commentAndToggleStateButton.text =
            TRANSLATIONS_BUNDLE.getString(
                "ui.button.comment_and_toggle_from_${
                    issue.state.toString().lowercase()
                }"
            )
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
        val issue = this.issue
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
                logErrorAndCreateDialogue("dialogue.error.getting_timeline", it).show()
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
        issue.getTimeline(latestTimelinePageShown++)?.forEachEvent {
            lastTimelinePageSize++
            getViewForEvent(EventContext(this, remoteContext, issue, it))?.let { node -> nodes.add(node) }
        }
        return nodes
    }

    private fun loadCommentCompositionBox() {
        // User must be a collaborator to toggle the state.
        if (!remoteContext.loggedInUserIsCollaborator) {
            commentButtonsBox.children.remove(commentAndToggleStateButton)
        }
        // If user not logged in and linked to platform or issue locked, do not show comment box.
        if (remoteContext.loggedInUser == null || issue.locked) {
            root.children.remove(commentBox)
        }
    }

    override fun toggleState() {
        getStateToggleFuture(issue)
            .thenAcceptOnMainThread {
                updateUIForToggledState()
            }
            .exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.toggle_issue_state", it).show()
            }
    }

    private fun updateUIForToggledState() {
        val createdAt = Date()
        issueUpdated()
        addStateChangeEventToTimeline(createdAt)
    }

    protected fun addStateChangeEventToTimeline(createdAt: Date) {
        addEventToTimeline(
            createStateChangeEvent(issue.state, remoteContext.loggedInUser!!, createdAt)
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun getStateToggleFuture(issue: I): CompletableFuture<Void?> =
        (if (issue.state == Issue.State.OPEN)
            issue.close()
        else issue.open())

    private fun createStateChangeEvent(newState: Issue.State, actor: User, createdAt: Date): Event {
        return Event.Raw(if (newState == Issue.State.OPEN) EventType.REOPENED else EventType.CLOSED, actor, createdAt)
    }

    override fun addEventToTimeline(event: Event) {
        getViewForEvent(EventContext(this, remoteContext, issue, event))?.let { view ->
            timelineBox.children.add(view.root)
        }
    }

    @FXML
    private fun commentAndToggleState(event: ActionEvent) {
        val body = commentField.text
        if (body.isNotEmpty()) {
            comment(event)
        }
        toggleState()
    }

    @FXML
    private fun comment(event: ActionEvent) {
        val body = commentField.text
        if (body.isNotEmpty()) {
            addComment(body)
        }
    }

    private fun addComment(body: String) {
        issue.addComment(body)
            .thenAcceptOnMainThread { comment ->
                issueUpdated()
                addCommentToTimeline(comment)
                commentField.text = ""
            }
            .exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.posting_issue_comment", it).show()
            }
    }

    private fun addCommentToTimeline(comment: Comment) {
        timelineBox.children.add(
            loadCommentView(this, this.remoteContext, this.issue, comment).root
        )
    }

    @FXML
    private fun addLabel(event: ActionEvent) {
        LabelSelectionContextMenu(remoteContext, issue) {
            addLabel(it)
        }.show(addLabelButton, Side.BOTTOM, 0.0, 0.0)
    }

    @Suppress("UNCHECKED_CAST")
    private fun addLabel(label: com.harleyoconnor.gitdesk.data.remote.Label) {
        issue.addLabel(label)
            .thenAcceptOnMainThread {
                val createdAt = Date()
                issueUpdated()
                addEventToTimeline(
                    LabeledEvent.Raw(
                        EventType.LABELED, remoteContext.loggedInUser!!, createdAt, label
                    )
                )
            }
            .exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.adding_assignee", it).show()
            }
    }

    @FXML
    private fun addAssignee(event: ActionEvent) {
        AssigneeSelectionContextMenu(remoteContext, issue) {
            addAssignee(it)
        }.show(assigneesBox, Side.BOTTOM, 0.0, 0.0)
    }

    @Suppress("UNCHECKED_CAST")
    private fun addAssignee(user: User) {
        issue.addAssignee(user)
            .thenAcceptOnMainThread {
                val createdAt = Date()
                issueUpdated()
                addAssigneeEventToTimeline(EventType.ASSIGNED, createdAt, user)
            }
            .exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.adding_assignee", it).show()
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun removeAssignee(user: User) {
        issue.removeAssignee(user)
            .thenAcceptOnMainThread {
                val createdAt = Date()
                issueUpdated()
                addAssigneeEventToTimeline(EventType.UNASSIGNED, createdAt, user)
            }
            .exceptionallyOnMainThread {
                logErrorAndCreateDialogue("dialogue.error.removing_assignee", it).show()
            }
    }

    private fun addAssigneeEventToTimeline(eventType: EventType, createdAt: Date, user: User) {
        addEventToTimeline(
            AssignedEvent.Raw(
                eventType, remoteContext.loggedInUser!!, createdAt, user
            )
        )
    }

}