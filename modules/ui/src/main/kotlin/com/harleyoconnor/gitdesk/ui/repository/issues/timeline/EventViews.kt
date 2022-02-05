package com.harleyoconnor.gitdesk.ui.repository.issues.timeline

import com.harleyoconnor.gitdesk.data.remote.Comment
import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.PullRequest
import com.harleyoconnor.gitdesk.data.remote.User
import com.harleyoconnor.gitdesk.data.remote.timeline.AssignedEvent
import com.harleyoconnor.gitdesk.data.remote.timeline.CommentedEvent
import com.harleyoconnor.gitdesk.data.remote.timeline.CommittedEvent
import com.harleyoconnor.gitdesk.data.remote.timeline.Event
import com.harleyoconnor.gitdesk.data.remote.timeline.EventType
import com.harleyoconnor.gitdesk.data.remote.timeline.LabeledEvent
import com.harleyoconnor.gitdesk.data.remote.timeline.MergedEvent
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.repository.issues.IssueAccessor
import com.harleyoconnor.gitdesk.ui.view.ViewLoader
import javafx.scene.Node
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class EventContext(
    val parent: IssueController<out Issue>,
    val remoteContext: RemoteContext,
    val issue: IssueAccessor<out Issue>,
    val event: Event
)

private val eventViews = mapOf<EventType, (EventContext) -> ViewLoader.View<*, out Node>>(
    EventType.COMMENTED to { context ->
        loadCommentView(context)
    },
    EventType.LABELED to { context ->
        loadLabeledEventView(context)
    },
    EventType.UNLABELED to { context ->
        loadLabeledEventView(context)
    },
    EventType.CLOSED to { context ->
        loadClosedEventView(context)
    },
    EventType.REOPENED to { context ->
        loadReOpenedEventView(context)
    },
    EventType.ASSIGNED to { context ->
        loadAssignedEventView(context)
    },
    EventType.UNASSIGNED to { context ->
        loadAssignedEventView(context)
    },
    EventType.COMMITTED to { context ->
        loadCommittedView(context)
    },
    EventType.MERGED to { context ->
        loadMergedView(context)
    }
)

fun getViewForEvent(context: EventContext): ViewLoader.View<*, out Node>? {
    return eventViews[context.event.type]?.invoke(context)
}

private fun loadCommentView(
    context: EventContext
): ViewLoader.View<out CommentController, VBox> {
    val commentedEvent = context.event as CommentedEvent
    return loadCommentView(
        context.parent,
        context.remoteContext,
        context.issue,
        Comment.Raw(
            commentedEvent.commentId,
            commentedEvent.body,
            commentedEvent.actor,
            commentedEvent.createdAt,
            commentedEvent.updatedAt
        )
    )
}

fun loadCommentView(
    parent: IssueController<out Issue>,
    remoteContext: RemoteContext,
    issue: IssueAccessor<out Issue>,
    comment: Comment
): ViewLoader.View<out CommentController, VBox> {
    val viewContext = CommentController.Context(
        parent, issue, comment
    )
    return if (
        canLoggedInUserModifyComment(
            comment.commenter,
            remoteContext.loggedInUser,
            remoteContext.loggedInUserIsCollaborator
        )
    ) {
        ModifiableCommentController.Loader.load(viewContext)
    } else CommentController.Loader.load(viewContext)
}

private fun canLoggedInUserModifyComment(
    commenter: User,
    loggedInUser: User?,
    loggedInUserIsCollaborator: Boolean
) = loggedInUserIsCollaborator || loggedInUser?.username == commenter.username

fun loadLabeledEventView(context: EventContext): ViewLoader.View<LabeledEventController, HBox> {
    val labeledEvent = context.event as LabeledEvent
    return LabeledEventController.Loader.load(
        LabeledEventController.Context(getFullLabelData(context, labeledEvent) ?: labeledEvent.label, labeledEvent)
    )
}

fun loadAssignedEventView(context: EventContext): ViewLoader.View<AssignedEventController, HBox> {
    val assignedEvent = context.event as AssignedEvent
    return AssignedEventController.Loader.load(
        AssignedEventController.Context(assignedEvent)
    )
}

/**
 * [LabeledEvent]'s label does not return the description of the label, if one is present, for GitHub's API. This
 * retrieves the full label data from the repository.
 */
private fun getFullLabelData(
    context: EventContext,
    labeledEvent: LabeledEvent
) = context.remoteContext.remote.getLabel(labeledEvent.label.name)

fun loadClosedEventView(context: EventContext): ViewLoader.View<ClosedEventController, HBox> {
    return ClosedEventController.Loader.load(
        ClosedEventController.Context(context.event)
    )
}

fun loadReOpenedEventView(context: EventContext): ViewLoader.View<ReOpenedEventController, HBox> {
    return ReOpenedEventController.Loader.load(
        ReOpenedEventController.Context(context.event)
    )
}

fun loadCommittedView(context: EventContext): ViewLoader.View<CommittedEventController, HBox> {
    val committedEvent = context.event as CommittedEvent
    return CommittedEventController.Loader.load(
        CommittedEventController.Context(committedEvent)
    )
}

@Suppress("UNCHECKED_CAST")
fun loadMergedView(context: EventContext): ViewLoader.View<MergedEventController, HBox> {
    val committedEvent = context.event as MergedEvent
    return MergedEventController.Loader.load(
        MergedEventController.Context(
            context.remoteContext,
            context.issue as IssueAccessor<PullRequest>,
            committedEvent
        )
    )
}

interface IssueController<I : Issue> {

    /**
     * Causes the UI to refresh the entire issue view from the remote.
     */
    fun refresh()

    /**
     * Updates UI for a change to the issue.
     *
     * @param issue the updated issue
     */
    fun issueUpdated(issue: I)

    fun addEventToTimeline(event: Event)

    fun toggleState()

    fun remove(node: Node)

}