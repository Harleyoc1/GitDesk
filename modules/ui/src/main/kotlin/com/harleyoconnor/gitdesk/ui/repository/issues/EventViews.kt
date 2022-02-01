package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Comment
import com.harleyoconnor.gitdesk.data.remote.timeline.CommentedEvent
import com.harleyoconnor.gitdesk.data.remote.timeline.Event
import com.harleyoconnor.gitdesk.data.remote.timeline.LabeledEvent
import com.harleyoconnor.gitdesk.ui.view.ViewLoader
import javafx.scene.Node
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

private val eventViews = mapOf<String, (TimelineController, IssueAccessor, Event) -> ViewLoader.View<*, out Node>>(
    "commented" to { _, issue, event ->
        loadCommentView(issue, event)
    },
    "labeled" to { _, _, event ->
        loadLabeledEventView(event)
    },
    "closed" to { _, _, event ->
        loadClosedEventView(event)
    },
    "reopened" to { _, _, event ->
        loadReOpenedEventView(event)
    }
)

fun getViewForEvent(parent: TimelineController, issue: IssueAccessor, event: Event): ViewLoader.View<*, out Node>? {
    return eventViews[event.id]?.invoke(parent, issue, event)
}

private fun loadCommentView(
    issue: IssueAccessor,
    event: Event
): ViewLoader.View<CommentController, VBox> {
    val commentedEvent = event as CommentedEvent
    return CommentController.Loader.load(
        CommentController.Context(
            issue,
            Comment.Raw(commentedEvent.body, commentedEvent.actor, commentedEvent.createdAt, commentedEvent.updatedAt)
        )
    )
}

fun loadLabeledEventView(event: Event): ViewLoader.View<LabeledEventController, HBox> {
    val labeledEvent = event as LabeledEvent
    return LabeledEventController.Loader.load(
        LabeledEventController.Context(labeledEvent)
    )
}

fun loadClosedEventView(event: Event): ViewLoader.View<ClosedEventController, HBox> {
    return ClosedEventController.Loader.load(
        ClosedEventController.Context(event)
    )
}

fun loadReOpenedEventView(event: Event): ViewLoader.View<ReOpenedEventController, HBox> {
    return ReOpenedEventController.Loader.load(
        ReOpenedEventController.Context(event)
    )
}

interface TimelineController {
    fun remove(node: Node)
}