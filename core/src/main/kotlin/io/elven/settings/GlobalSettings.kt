package io.elven.settings

import java.io.File
import java.io.IOException


open class GlobalSettings(
    var anilistUserName: String,
    var anilistToken: String,
    val logLevel: String,
    val logPath: String,
    val pathToAnimes: String = ""
) {
    init {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, logLevel)
        if (!logPath.isNullOrEmpty()) {
            try {
                File(logPath).createNewFile()
                System.setProperty(org.slf4j.impl.SimpleLogger.LOG_FILE_KEY, logPath)
            } catch (exception: IOException) {
                System.err.println("Cannot create log file: $logPath")
            }
        }
        System.setProperty(org.slf4j.impl.SimpleLogger.SHOW_DATE_TIME_KEY, true.toString())
        System.setProperty(org.slf4j.impl.SimpleLogger.DATE_TIME_FORMAT_KEY, "dd/MM HH:mm :")
    }
}