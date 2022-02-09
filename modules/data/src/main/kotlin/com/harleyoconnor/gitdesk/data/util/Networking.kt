package com.harleyoconnor.gitdesk.data.util

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.account.Session
import com.harleyoconnor.gitdesk.util.network.HttpHeader.SESSION_KEY
import com.harleyoconnor.gitdesk.util.network.URIBuilder
import com.harleyoconnor.gitdesk.util.network.mapOrElseThrow
import java.lang.reflect.Type
import java.net.http.HttpRequest
import java.net.http.HttpResponse

internal fun startBuildingUri(): URIBuilder {
    return URIBuilder()
        .https()
        .append("harleyoconnor.com/gitdesk/")
}

/**
 * Adds GitDesk session key to the header if a session is currently active.
 */
fun HttpRequest.Builder.addSessionHeader(): HttpRequest.Builder {
    Session.getOrLoad()?.let {
        this.header(SESSION_KEY, it.key)
    }
    return this
}

fun <T> HttpResponse<String>.mapToJsonOrThrow(type: Class<T>, messageSupplier: () -> String): T {
    return mapOrElseThrow({
        MOSHI.adapter(type).fromJson(it)!!
    }, messageSupplier)
}

fun <T> HttpResponse<String>.mapToJsonOrThrow(type: Type, messageSupplier: () -> String): T {
    return mapOrElseThrow({
        MOSHI.adapter<T>(type).fromJson(it)!!
    }, messageSupplier)
}