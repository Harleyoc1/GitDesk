package com.harleyoconnor.gitdesk.data.remote.checklist

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.RepositoryOwner
import com.harleyoconnor.gitdesk.data.util.addSessionHeader
import com.harleyoconnor.gitdesk.data.util.mapToJsonOrThrow
import com.harleyoconnor.gitdesk.data.util.startBuildingUri
import com.harleyoconnor.gitdesk.util.network.CLIENT
import com.harleyoconnor.gitdesk.util.network.HttpHeader
import com.harleyoconnor.gitdesk.util.network.PATCH
import com.harleyoconnor.gitdesk.util.network.httpFormData
import com.harleyoconnor.gitdesk.util.network.orElseThrow
import com.harleyoconnor.gitdesk.util.network.toHttpFormData
import com.harleyoconnor.gitdesk.util.with
import com.squareup.moshi.Json
import com.squareup.moshi.Types
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture

class RepositoryData(
    private val ownerName: String,
    private val name: String,
    private val platform: String,
    private val ownerIsOrg: Boolean
) {
    companion object {
        fun forRemote(remote: RemoteRepository): RepositoryData {
            return RepositoryData(
                remote.name.ownerName,
                remote.name.repositoryName,
                remote.platform.serverName!!,
                remote.owner.type == RepositoryOwner.Type.ORGANISATION
            )
        }
    }

    fun createMap(): Map<String, String> {
        return mapOf(
            "repository_owner" to ownerName,
            "repository_name" to name,
            "repository_platform" to platform,
            "repository_owner_org" to ownerIsOrg.toString()
        )
    }
}

class AssigneeData(
    val username: String,
    @Json(name = "github_username") val gitHubUsername: String?
)

/**
 * Sends a request that returns potential checklist item assignees for a given [remote] repository.
 */
fun getAssignees(
    remote: RemoteRepository
): CompletableFuture<List<AssigneeData>> {
    return CLIENT.sendAsync(
        createGetAssigneesRequest(
            RepositoryData.forRemote(remote)
        ),
        HttpResponse.BodyHandlers.ofString()
    ).thenApply {
        it.mapToJsonOrThrow(Types.newParameterizedType(List::class.java, AssigneeData::class.java)) {
            "Retrieving potential assignees."
        }
    }
}

private fun createGetAssigneesRequest(
    repositoryData: RepositoryData
): HttpRequest {
    return HttpRequest.newBuilder()
        .GET()
        .uri(
            startBuildingUri()
                .append("repository/assignees/")
                .parameters(repositoryData.createMap())
                .build()
        )
        .addSessionHeader()
        .header(HttpHeader.ACCEPT, HttpHeader.JSON)
        .build()
}


class ChecklistCreationData(
    private val name: String,
    private val description: String?
) {
    fun createMap(): Map<String, String> {
        return mapOf(
            "name" to name,
            "description" to description.toString()
        )
    }
}

fun postChecklist(
    remote: RemoteRepository,
    name: String,
    description: String?
): CompletableFuture<Checklist> {
    return CLIENT.sendAsync(
        createPostChecklistRequest(
            RepositoryData.forRemote(remote),
            ChecklistCreationData(name, description)
        ),
        HttpResponse.BodyHandlers.ofString()
    ).thenApply {
        it.mapToJsonOrThrow(Checklist::class.java) {
            "Posting new checklist."
        }
    }
}

private fun createPostChecklistRequest(
    repositoryData: RepositoryData,
    checklistCreationData: ChecklistCreationData
): HttpRequest {
    return HttpRequest.newBuilder()
        .POST(
            HttpRequest.BodyPublishers.ofString(
                (repositoryData.createMap() with checklistCreationData.createMap()).toHttpFormData()
            )
        )
        .uri(
            startBuildingUri()
                .append("repository/checklist/")
                .build()
        )
        .addSessionHeader()
        .header(HttpHeader.CONTENT_TYPE, HttpHeader.FORM_ENCODED)
        .header(HttpHeader.ACCEPT, HttpHeader.JSON)
        .build()
}

fun checklistExists(
    remote: RemoteRepository,
    name: String
): CompletableFuture<Boolean?> {
    return CLIENT.sendAsync(
        createGetChecklistExistsRequest(
            RepositoryData.forRemote(remote),
            name
        ),
        HttpResponse.BodyHandlers.discarding()
    ).thenApply {
        if (it.statusCode() !in 200 until 300) {
            null
        } else it.statusCode() == 210 // 210 means the checklist exists.
    }
}

private fun createGetChecklistExistsRequest(
    repositoryData: RepositoryData, name: String
): HttpRequest {
    return HttpRequest.newBuilder()
        .GET()
        .uri(
            startBuildingUri()
                .append("repository/checklist/exists/")
                .parameters(repositoryData.createMap())
                .parameter("checklist", name)
                .build()
        )
        .addSessionHeader()
        .header(HttpHeader.ACCEPT, HttpHeader.JSON)
        .build()
}

fun getChecklists(
    remote: RemoteRepository
): CompletableFuture<List<Checklist>> {
    return CLIENT.sendAsync(
        createGetChecklistsRequest(
            RepositoryData.forRemote(remote)
        ),
        HttpResponse.BodyHandlers.ofString()
    ).thenApply {
        it.mapToJsonOrThrow(Types.newParameterizedType(List::class.java, Checklist::class.java)) {
            "Retrieving checklists."
        }
    }
}

private fun createGetChecklistsRequest(
    repositoryData: RepositoryData
): HttpRequest {
    return HttpRequest.newBuilder()
        .GET()
        .uri(
            startBuildingUri()
                .append("repository/checklists/")
                .parameters(repositoryData.createMap())
                .build()
        )
        .addSessionHeader()
        .header(HttpHeader.ACCEPT, HttpHeader.JSON)
        .build()
}

fun deleteChecklist(
    remote: RemoteRepository,
    checklist: Checklist
): CompletableFuture<Void?> {
    return CLIENT.sendAsync(
        createDeleteChecklistRequest(
            RepositoryData.forRemote(remote),
            ChecklistData(checklist.name)
        ),
        HttpResponse.BodyHandlers.discarding()
    ).thenApply {
        it.orElseThrow {
            "Deleting checklist."
        }
    }
}

private fun createDeleteChecklistRequest(
    repositoryData: RepositoryData,
    checklistData: ChecklistData
): HttpRequest {
    return HttpRequest.newBuilder()
        .DELETE()
        .uri(
            startBuildingUri()
                .append("repository/checklist/")
                .parameters(repositoryData.createMap())
                .parameters(checklistData.createMap())
                .build()
        )
        .addSessionHeader()
        .header(HttpHeader.CONTENT_TYPE, HttpHeader.FORM_ENCODED)
        .build()
}

class ChecklistData(
    private val name: String
) {
    fun createMap(): Map<String, String> {
        return mapOf(
            "checklist" to name
        )
    }
}

class ChecklistItemCreationData(
    private val title: String,
    private val body: String
) {
    fun createMap(): Map<String, String> {
        return mapOf(
            "title" to title,
            "body" to body
        )
    }
}

fun postChecklistItem(
    remote: RemoteRepository,
    checklist: Checklist,
    title: String,
    body: String
): CompletableFuture<ChecklistItem> {
    return CLIENT.sendAsync(
        createPostChecklistItemRequest(
            RepositoryData.forRemote(remote),
            ChecklistData(checklist.name),
            ChecklistItemCreationData(title, body)
        ),
        HttpResponse.BodyHandlers.ofString()
    ).thenApply {
        it.mapToJsonOrThrow(ChecklistItem::class.java) {
            "Posting new checklist item."
        }
    }
}

private fun createPostChecklistItemRequest(
    repositoryData: RepositoryData,
    checklistData: ChecklistData,
    checklistItemCreationData: ChecklistItemCreationData
): HttpRequest {
    return HttpRequest.newBuilder()
        .POST(
            HttpRequest.BodyPublishers.ofString(
                (repositoryData.createMap() with checklistItemCreationData.createMap() with checklistData.createMap())
                    .toHttpFormData()
            )
        )
        .uri(
            startBuildingUri()
                .append("repository/checklist/item/")
                .build()
        )
        .addSessionHeader()
        .header(HttpHeader.CONTENT_TYPE, HttpHeader.FORM_ENCODED)
        .header(HttpHeader.ACCEPT, HttpHeader.JSON)
        .build()
}

fun getChecklistItems(
    remote: RemoteRepository,
    checklist: Checklist
): CompletableFuture<List<ChecklistItem>> {
    return CLIENT.sendAsync(
        createGetChecklistItemsRequest(
            RepositoryData.forRemote(remote),
            ChecklistData(checklist.name)
        ),
        HttpResponse.BodyHandlers.ofString()
    ).thenApply {
        it.mapToJsonOrThrow(Types.newParameterizedType(List::class.java, ChecklistItem::class.java)) {
            "Retrieving checklist items."
        }
    }
}

private fun createGetChecklistItemsRequest(
    repositoryData: RepositoryData,
    checklistData: ChecklistData
): HttpRequest {
    return HttpRequest.newBuilder()
        .GET()
        .uri(
            startBuildingUri()
                .append("repository/checklist/items/")
                .parameters(repositoryData.createMap())
                .parameters(checklistData.createMap())
                .build()
        )
        .addSessionHeader()
        .header(HttpHeader.CONTENT_TYPE, HttpHeader.FORM_ENCODED)
        .header(HttpHeader.ACCEPT, HttpHeader.JSON)
        .build()
}

fun patchChecklistItem(
    remote: RemoteRepository,
    checklist: Checklist,
    checklistItem: ChecklistItem
): CompletableFuture<Void?> {
    return CLIENT.sendAsync(
        createPatchChecklistItemRequest(
            RepositoryData.forRemote(remote),
            ChecklistData(checklist.name),
            checklistItem
        ),
        HttpResponse.BodyHandlers.discarding()
    ).thenApply {
        it.orElseThrow {
            "Patching checklist item."
        }
    }
}

private fun createPatchChecklistItemRequest(
    repositoryData: RepositoryData,
    checklistData: ChecklistData,
    checklistItem: ChecklistItem
): HttpRequest {
    return HttpRequest.newBuilder()
        .PATCH(
            httpFormData(repositoryData.createMap(), checklistData.createMap(), checklistItem.createPatchMap())
        )
        .uri(
            startBuildingUri()
                .append("repository/checklist/item/")
                .build()
        )
        .addSessionHeader()
        .header(HttpHeader.CONTENT_TYPE, HttpHeader.FORM_ENCODED)
        .build()
}

fun deleteChecklistItem(
    remote: RemoteRepository,
    checklist: Checklist,
    checklistItem: ChecklistItem
): CompletableFuture<Void?> {
    return CLIENT.sendAsync(
        createDeleteChecklistItemRequest(
            RepositoryData.forRemote(remote),
            ChecklistData(checklist.name),
            checklistItem
        ),
        HttpResponse.BodyHandlers.discarding()
    ).thenApply {
        it.orElseThrow {
            "Deleting checklist item."
        }
    }
}

private fun createDeleteChecklistItemRequest(
    repositoryData: RepositoryData,
    checklistData: ChecklistData,
    checklistItem: ChecklistItem
): HttpRequest {
    return HttpRequest.newBuilder()
        .DELETE()
        .uri(
            startBuildingUri()
                .append("repository/checklist/item/")
                .parameters(repositoryData.createMap())
                .parameters(checklistData.createMap())
                .parameter("item_id", checklistItem.id.toString())
                .build()
        )
        .addSessionHeader()
        .header(HttpHeader.CONTENT_TYPE, HttpHeader.FORM_ENCODED)
        .build()
}

class ChecklistItemData(
    private val id: Int
) {
    fun createMap(): Map<String, String> = mapOf("item_id" to id.toString())
}

fun getChecklistItemComments(
    remote: RemoteRepository,
    checklist: Checklist,
    checklistItem: ChecklistItem
): CompletableFuture<List<ChecklistItemComment>> {
    return CLIENT.sendAsync(
        createGetChecklistItemCommentsRequest(
            RepositoryData.forRemote(remote),
            ChecklistData(checklist.name),
            ChecklistItemData(checklistItem.id)
        ),
        HttpResponse.BodyHandlers.ofString()
    ).thenApply {
        it.mapToJsonOrThrow(Types.newParameterizedType(List::class.java, ChecklistItemComment::class.java)) {
            "Retrieving checklist item comments."
        }
    }
}

private fun createGetChecklistItemCommentsRequest(
    repositoryData: RepositoryData,
    checklistData: ChecklistData,
    checklistItemData: ChecklistItemData
): HttpRequest {
    return HttpRequest.newBuilder()
        .GET()
        .uri(
            startBuildingUri()
                .append("repository/checklist/item/comments/")
                .parameters(repositoryData.createMap())
                .parameters(checklistData.createMap())
                .parameters(checklistItemData.createMap())
                .build()
        )
        .addSessionHeader()
        .header(HttpHeader.CONTENT_TYPE, HttpHeader.FORM_ENCODED)
        .header(HttpHeader.ACCEPT, HttpHeader.JSON)
        .build()
}

fun postChecklistItemComment(
    remote: RemoteRepository,
    checklist: Checklist,
    checklistItem: ChecklistItem,
    body: String
): CompletableFuture<ChecklistItemComment> {
    return CLIENT.sendAsync(
        createPostChecklistItemCommentRequest(
            RepositoryData.forRemote(remote),
            ChecklistData(checklist.name),
            ChecklistItemData(checklistItem.id),
            body
        ),
        HttpResponse.BodyHandlers.ofString()
    ).thenApply {
        it.mapToJsonOrThrow(ChecklistItemComment::class.java) {
            "Posting checklist item comment."
        }
    }
}

private fun createPostChecklistItemCommentRequest(
    repositoryData: RepositoryData,
    checklistData: ChecklistData,
    checklistItemData: ChecklistItemData,
    body: String
): HttpRequest {
    return HttpRequest.newBuilder()
        .POST(
            HttpRequest.BodyPublishers.ofString(
                (repositoryData.createMap() with checklistData.createMap() with checklistItemData.createMap()
                        with mapOf("body" to body))
                    .toHttpFormData()
            )
        )
        .uri(
            startBuildingUri().append("repository/checklist/item/comment/").build()
        )
        .addSessionHeader()
        .header(HttpHeader.CONTENT_TYPE, HttpHeader.FORM_ENCODED)
        .header(HttpHeader.ACCEPT, HttpHeader.JSON)
        .build()
}

class ChecklistItemCommentData(
    private val id: Int
) {
    fun createMap(): Map<String, String> = mapOf("comment_id" to id.toString())
}

fun patchChecklistItemComment(
    remote: RemoteRepository,
    checklist: Checklist,
    checklistItem: ChecklistItem,
    checklistItemComment: ChecklistItemComment
): CompletableFuture<Void?> {
    return CLIENT.sendAsync(
        createPatchChecklistItemRequest(
            RepositoryData.forRemote(remote),
            ChecklistData(checklist.name),
            ChecklistItemData(checklistItem.id),
            checklistItemComment
        ),
        HttpResponse.BodyHandlers.discarding()
    ).thenApply {
        it.orElseThrow {
            "Patching checklist item."
        }
    }
}

private fun createPatchChecklistItemRequest(
    repositoryData: RepositoryData,
    checklistData: ChecklistData,
    checklistItemData: ChecklistItemData,
    checklistItemComment: ChecklistItemComment
): HttpRequest {
    return HttpRequest.newBuilder()
        .PATCH(
            httpFormData(
                repositoryData.createMap(),
                checklistData.createMap(),
                checklistItemData.createMap(),
                checklistItemComment.createPatchMap()
            )
        )
        .uri(
            startBuildingUri().append("repository/checklist/item/comment/").build()
        )
        .addSessionHeader()
        .header(HttpHeader.CONTENT_TYPE, HttpHeader.FORM_ENCODED)
        .build()
}

fun deleteChecklistItemComment(
    remote: RemoteRepository,
    checklist: Checklist,
    checklistItem: ChecklistItem,
    checklistItemComment: ChecklistItemComment
): CompletableFuture<Void?> {
    return CLIENT.sendAsync(
        createDeleteChecklistItemCommentRequest(
            RepositoryData.forRemote(remote),
            ChecklistData(checklist.name),
            ChecklistItemData(checklistItem.id),
            ChecklistItemCommentData(checklistItemComment.id)
        ),
        HttpResponse.BodyHandlers.discarding()
    ).thenApply {
        it.orElseThrow {
            "Deleting checklist item comment."
        }
    }
}

private fun createDeleteChecklistItemCommentRequest(
    repositoryData: RepositoryData,
    checklistData: ChecklistData,
    checklistItemData: ChecklistItemData,
    checklistItemCommentData: ChecklistItemCommentData
): HttpRequest {
    return HttpRequest.newBuilder()
        .DELETE()
        .uri(
            startBuildingUri()
                .append("repository/checklist/item/comment/")
                .parameters(repositoryData.createMap())
                .parameters(checklistData.createMap())
                .parameters(checklistItemData.createMap())
                .parameters(checklistItemCommentData.createMap())
                .build()
        )
        .addSessionHeader()
        .header(HttpHeader.CONTENT_TYPE, HttpHeader.FORM_ENCODED)
        .build()
}

fun postChecklistItemAssignee(
    remote: RemoteRepository,
    checklist: Checklist,
    checklistItem: ChecklistItem,
    assigneeUsername: String
): CompletableFuture<ChecklistItemAssignee> {
    return CLIENT.sendAsync(
        createPostChecklistItemAssigneeRequest(
            RepositoryData.forRemote(remote),
            ChecklistData(checklist.name),
            ChecklistItemData(checklistItem.id),
            assigneeUsername
        ),
        HttpResponse.BodyHandlers.ofString()
    ).thenApply {
        it.mapToJsonOrThrow(ChecklistItemAssignee::class.java) {
            "Posting checklist item assignee."
        }
    }
}

private fun createPostChecklistItemAssigneeRequest(
    repositoryData: RepositoryData,
    checklistData: ChecklistData,
    checklistItemData: ChecklistItemData,
    assigneeUsername: String
): HttpRequest {
    return HttpRequest.newBuilder()
        .POST(
            HttpRequest.BodyPublishers.ofString(
                (repositoryData.createMap() with checklistData.createMap() with checklistItemData.createMap()
                        with mapOf("assignee_username" to assigneeUsername))
                    .toHttpFormData()
            )
        )
        .uri(
            startBuildingUri().append("repository/checklist/item/assignee/").build()
        )
        .addSessionHeader()
        .header(HttpHeader.CONTENT_TYPE, HttpHeader.FORM_ENCODED)
        .header(HttpHeader.ACCEPT, HttpHeader.JSON)
        .build()
}

fun deleteChecklistItemAssignee(
    remote: RemoteRepository,
    checklist: Checklist,
    checklistItem: ChecklistItem,
    assigneeUsername: String
): CompletableFuture<Void?> {
    return CLIENT.sendAsync(
        createDeleteChecklistItemAssigneeRequest(
            RepositoryData.forRemote(remote),
            ChecklistData(checklist.name),
            ChecklistItemData(checklistItem.id),
            assigneeUsername
        ),
        HttpResponse.BodyHandlers.discarding()
    ).thenApply {
        it.orElseThrow {
            "Deleting checklist item assignee."
        }
    }
}

private fun createDeleteChecklistItemAssigneeRequest(
    repositoryData: RepositoryData,
    checklistData: ChecklistData,
    checklistItemData: ChecklistItemData,
    assigneeUsername: String
): HttpRequest {
    return HttpRequest.newBuilder()
        .DELETE()
        .uri(
            startBuildingUri()
                .append("repository/checklist/item/assignee/")
                .parameters(repositoryData.createMap())
                .parameters(checklistData.createMap())
                .parameters(checklistItemData.createMap())
                .parameter("assignee_username", assigneeUsername)
                .build()
        )
        .addSessionHeader()
        .header(HttpHeader.CONTENT_TYPE, HttpHeader.FORM_ENCODED)
        .build()
}