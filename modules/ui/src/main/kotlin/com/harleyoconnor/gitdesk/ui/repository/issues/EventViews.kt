package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.data.remote.IssueComment
import com.harleyoconnor.gitdesk.data.remote.timeline.CommentedEvent
import com.harleyoconnor.gitdesk.data.remote.timeline.Event
import com.harleyoconnor.gitdesk.data.remote.timeline.LabeledEvent
import com.harleyoconnor.gitdesk.ui.view.ViewLoader
import javafx.scene.Node
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

private val eventViews = mapOf<String, (TimelineController, Issue, Event) -> ViewLoader.View<*, out Node>>(
    "commented" to { _, issue, event ->
        loadCommentView(issue, event)
    },
    "labeled" to { _, _, event ->
        loadLabeledEventView(event)
    }
)

fun getViewForEvent(parent: TimelineController, issue: Issue, event: Event): ViewLoader.View<*, out Node>? {
    return eventViews[event.id]?.invoke(parent, issue, event)
}

private fun loadCommentView(
    issue: Issue,
    event: Event
): ViewLoader.View<CommentController, VBox> {
    val commentedEvent = event as CommentedEvent
    return CommentController.Loader.load(
        CommentController.Context(
            issue,
            IssueComment(commentedEvent.body, commentedEvent.actor, commentedEvent.createdAt, commentedEvent.updatedAt)
        )
    )
}

fun loadLabeledEventView(event: Event): ViewLoader.View<LabeledEventController, HBox> {
    val labeledEvent = event as LabeledEvent
    return LabeledEventController.Loader.load(
        LabeledEventController.Context(labeledEvent)
    )
}

interface TimelineController {
    fun remove(node: Node)
}