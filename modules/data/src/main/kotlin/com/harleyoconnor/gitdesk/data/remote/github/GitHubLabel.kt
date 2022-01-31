package com.harleyoconnor.gitdesk.data.remote.github

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.remote.Label
import com.harleyoconnor.gitdesk.data.serialisation.qualifier.HexColour
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter

/**
 *
 * @author Harley O'Connor
 */
class GitHubLabel(
    override val name: String,
    @HexColour @Json(name = "color") override val colour: Int,
    override val description: String?
): Label {

    companion object {
        val ADAPTER: JsonAdapter<GitHubLabel> by lazy { MOSHI.adapter(GitHubLabel::class.java) }
    }

}