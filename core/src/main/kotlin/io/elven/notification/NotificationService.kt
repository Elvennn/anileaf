package io.elven.notification

import io.elven.anilist.Anilist
import io.elven.anitomy.AnimeFile
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

abstract class NotificationService {
    companion object {
        private val httpClient: HttpClient = HttpClient.newBuilder().build()
        private val logger = LoggerFactory.getLogger(NotificationService::class.java)
    }

    fun notifyAnime(animeFile: AnimeFile) {
        send("Torrent downloaded : ${animeFile.title} episode ${animeFile.episode}")
    }

    private fun send(message: String) {
        logger.info("send notification : $message")
        httpClient.sendAsync(
            HttpRequest.newBuilder()
                .uri(
                    URI.create(
                        createURI(message)
                    )
                )
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).thenApply {
            logger.info("notification response code : ${it.statusCode()}")
        }
    }

    abstract fun createURI(message: String): String
}