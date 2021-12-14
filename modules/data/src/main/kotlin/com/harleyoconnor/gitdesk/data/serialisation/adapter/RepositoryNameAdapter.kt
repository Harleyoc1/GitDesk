package com.harleyoconnor.gitdesk.data.serialisation.adapter

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/**
 * @author Harley O'Connor
 */
object RepositoryNameAdapter {

    @ToJson
    fun toJson(name: RemoteRepository.Name): String {
        return name.getFullName()
    }

    @FromJson
    fun fromJson(fullName: String): RemoteRepository.Name {
        return RemoteRepository.Name(fullName.substringBefore('/'), fullName.substringAfter('/'))
    }

}