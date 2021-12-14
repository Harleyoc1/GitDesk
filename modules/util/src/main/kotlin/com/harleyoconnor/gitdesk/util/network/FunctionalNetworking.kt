package com.harleyoconnor.gitdesk.util.network

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

val CLIENT: HttpClient = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_2)
    .build()

fun getJsonAt(uri: URI): HttpResponse<String> {
    return CLIENT.send(
        HttpRequest.newBuilder()
            .GET()
            .uri(uri)
            .setHeader(HttpHeader.ACCEPT, HttpHeader.JSON)
            .build(),
        HttpResponse.BodyHandlers.ofString()
    )
}