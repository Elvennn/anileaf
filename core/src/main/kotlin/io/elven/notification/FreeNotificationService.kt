package io.elven.notification

import java.net.URLEncoder

class FreeNotificationService(private val user: String, private val pass: String) : NotificationService() {
    companion object {
        private const val notificationURL = "https://smsapi.free-mobile.fr/sendmsg"
    }

    override fun createURI(message: String) =
        "$notificationURL?user=${user}&pass=${pass}&msg=${URLEncoder.encode(
            message,
            "UTF-8"
        )}"
}