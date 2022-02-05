package com.harleyoconnor.gitdesk.data.remote.timeline

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import java.net.URL

/**
 *
 * @author Harley O'Connor
 */
interface MergedEvent : Event {

    val commitId: String

    fun getUrl(parentName: RemoteRepository.Name): URL

}