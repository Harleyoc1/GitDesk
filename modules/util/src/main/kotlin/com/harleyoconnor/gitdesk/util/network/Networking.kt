package com.harleyoconnor.gitdesk.util.network

import com.harleyoconnor.gitdesk.util.stream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.stream.Collectors

val CLIENT: HttpClient = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_2)
    .build()

fun HttpRequest.Builder.DELETE(bodyPublisher: HttpRequest.BodyPublisher): HttpRequest.Builder {
    return this.method("DELETE", bodyPublisher)
}

fun HttpRequest.Builder.PATCH(bodyPublisher: HttpRequest.BodyPublisher): HttpRequest.Builder {
    return this.method("PATCH", bodyPublisher)
}

fun HttpRequest.Builder.uri(uri: String): HttpRequest.Builder {
    this.uri(URI.create(uri))
    return this
}

fun <T, N> HttpResponse<T>.mapOrElseThrow(mapper: (T) -> N, messageSupplier: () -> String): N {
    return if (this.statusCode() in 200 until 300 && this.body() != null) {
        mapper(this.body())
    } else throw HttpRequestException(messageSupplier(), this.statusCode(), this.body())
}

fun <T> HttpResponse<T>.orElseThrow(messageSupplier: () -> String): T {
    return if (this.statusCode() in 200 until 300) {
        this.body()
    } else throw HttpRequestException(messageSupplier(), this.statusCode(), this.body())
}

fun Map<out Any?, Any?>.toHttpFormData(): String {
    return this.entries.stream()
        .map { "${it.key}=${it.value}" }
        .collect(Collectors.joining("&"))
}

fun httpFormData(vararg maps: Map<out Any?, Any?>): HttpRequest.BodyPublisher {
    return HttpRequest.BodyPublishers.ofString(
        maps.stream()
            .map { it.toHttpFormData() }
            .collect(Collectors.joining("&"))
    )
}

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