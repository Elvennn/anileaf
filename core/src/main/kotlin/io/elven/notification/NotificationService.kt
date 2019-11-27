package io.elven.notification

import io.elven.anitomy.AnimeFile
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

abstract class NotificationService {
    companion object {
        private val httpClient: HttpClient = HttpClient.newBuilder().build()
    }

    fun notifyAnime(animeFile: AnimeFile) {
        send("Torrent downloaded : ${animeFile.title} episode ${animeFile.episode}")
    }

    private fun send(message: String) {
        httpClient.sendAsync(
            HttpRequest.newBuilder()
                .uri(
                    URI.create(
                        createURI(message)
                    )
                )
                .build(),
            HttpResponse.BodyHandlers.ofString()
        )
    }

    abstract fun createURI(message: String): String
}