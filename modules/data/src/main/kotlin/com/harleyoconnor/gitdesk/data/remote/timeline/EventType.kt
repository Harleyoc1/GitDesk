package com.harleyoconnor.gitdesk.data.remote.timeline

import com.harleyoconnor.gitdesk.util.stream

/**
 * Stores all types for [Event]s and their identifiers on supported platforms.
 *
 * @author Harley O'Connor
 */
enum class EventType(
    val gitHubId: String
) {
    ADDED_TO_PROJECT("added_to_project"),
    ASSIGNED("assigned"),
    AUTOMATIC_BASE_CHANGE_FAILED("automatic_base_change_failed"),
    AUTOMATIC_BASE_CHANGE_SUCCEEDED("automatic_base_change_succeeded"),
    BASE_REF_CHANGED("base_ref_changed"),
    CLOSED("closed"),
    COMMENTED("commented"),
    COMMITTED("committed"),
    CONNECTED("connected"),
    CONVERT_TO_DRAFT("convert_to_draft"),
    CONVERTED_NOTE_TO_ISSUE("converted_note_to_issue"),
    CROSS_REFERENCED("cross-referenced"),
    DEMILESTONED("demilestoned"),
    DEPLOYED("deployed"),
    DEPLOYMENT_ENVIRONMENT_CHANGED("deployment_environment_changed"),
    DISCONNECTED("disconnected"),
    HEAD_REF_DELETED("head_ref_deleted"),
    HEAD_REF_RESTORED("head_ref_restored"),
    LABELED("labeled"),
    LOCKED("locked"),
    MENTIONED("mentioned"),
    MARKED_AS_DUPLICATE("marked_as_duplicate"),
    MERGED("merged"),
    MILESTONED("milestoned"),
    MOVED_COLUMNS_IN_PROJECT("moved_columns_in_project"),
    PINNED("pinned"),
    READY_FOR_REVIEW("ready_for_review"),
    REFERENCED("referenced"),
    REMOVED_FROM_PROJECT("removed_from_project"),
    RENAMED("renamed"),
    REOPENED("reopened"),
    REVIEW_DISMISSED("review_dismissed"),
    REVIEW_REQUESTED("review_requested"),
    REVIEW_REQUEST_REMOVED("review_request_removed"),
    REVIEWED("reviewed"),
    SUBSCRIBED("subscribed"),
    TRANSFERRED("transferred"),
    UNASSIGNED("unassigned"),
    UNLABELED("unlabeled"),
    UNLOCKED("unlocked"),
    UNMARK_AS_DUPLICATE("unmarked_as_duplicate"),
    UNPINNED("unpinned"),
    UNSUBSCRIBED("unsubscribed"),
    USER_BLOCKED("user_blocked");

    companion object {
        fun fromGitHubId(id: String): EventType? {
            return values().stream()
                .filter { it.gitHubId == id }
                .findFirst()
                .orElse(null)
        }
    }
}