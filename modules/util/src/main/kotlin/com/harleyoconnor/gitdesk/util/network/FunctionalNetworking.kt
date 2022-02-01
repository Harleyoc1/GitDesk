package com.harleyoconnor.gitdesk.util.network

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

val CLIENT: HttpClient = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_2)
    .build()

fun sendRequest(uri: URI): HttpResponse<Void> {
    return CLIENT.send(
        HttpRequest.newBuilder()
            .GET()
            .uri(uri)
            .build(),
        HttpResponse.BodyHandlers.discarding()
    )
}

fun sendRequest(uri: URI, authHeader: String): HttpResponse<Void> {
    return CLIENT.send(
        HttpRequest.newBuilder()
            .GET()
            .uri(uri)
            .header(HttpHeader.AUTHORIZATION, authHeader)
            .build(),
        HttpResponse.BodyHandlers.discarding()
    )
}

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

fun getJsonAt(uri: URI, authHeader: String): HttpResponse<String> {
    return CLIENT.send(
        HttpRequest.newBuilder()
            .GET()
            .uri(uri)
            .setHeader(HttpHeader.ACCEPT, HttpHeader.JSON)
            .setHeader(HttpHeader.AUTHORIZATION, authHeader)
            .build(),
        HttpResponse.BodyHandlers.ofString()
    )
}