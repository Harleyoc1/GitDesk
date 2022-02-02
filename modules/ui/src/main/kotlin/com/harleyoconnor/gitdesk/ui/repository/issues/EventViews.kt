package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Comment
import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.User
import com.harleyoconnor.gitdesk.data.remote.timeline.CommentedEvent
import com.harleyoconnor.gitdesk.data.remote.timeline.Event
import com.harleyoconnor.gitdesk.data.remote.timeline.EventType
import com.harleyoconnor.gitdesk.data.remote.timeline.LabeledEvent
import com.harleyoconnor.gitdesk.ui.repository.RemoteContext
import com.harleyoconnor.gitdesk.ui.view.ViewLoader
import javafx.scene.Node
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class EventContext(
    val parent: TimelineController,
    val remoteContext: RemoteContext,
    val issue: IssueAccessor,
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
    parent: TimelineController,
    remoteContext: RemoteContext,
    issue: IssueAccessor,
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

interface TimelineController {

    /**
     * Updates UI for a change to the issue.
     *
     * @param issue the updated issue
     */
    fun issueUpdated(issue: Issue)

    fun addLabeledEventToTimeline(labeledEvent: LabeledEvent)

    fun remove(node: Node)

}