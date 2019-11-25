package io.elven.notification

import io.elven.anitomy.AnimeFile
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class NotificationService {
    companion object {
        private val httpClient: HttpClient = HttpClient.newBuilder().build()
        private const val notificationURL = "https://smsapi.free-mobile.fr/sendmsg"
    }

    fun notifyAnime(animeFile: AnimeFile) {
        send("Torrent downloaded : ${animeFile.title} episode ${animeFile.episode}")
    }

    private fun send(message: String) {
        httpClient.sendAsync(
            HttpRequest.newBuilder()
                .uri(
                    URI.create(
                        "$notificationURL?user=${NotificationCredentials.user}&pass=${NotificationCredentials.pass}&msg=${URLEncoder.encode(
                            message,
                            "UTF-8"
                        )}"
                    )
                )
                .build(),
            HttpResponse.BodyHandlers.ofString()
        )
    }
}