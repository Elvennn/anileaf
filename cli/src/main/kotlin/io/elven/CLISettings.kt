package io.elven

import io.elven.settings.DataFileHandler
import io.elven.settings.GlobalSettings

class CLISettings(
    anilistUserName: String = "",
    anilistToken: String = "",
    logLevel: String = "INFO",
    logPath: String = "/tmp/anileaf.log"
) : GlobalSettings(anilistUserName, anilistToken, logLevel, logPath) {
    companion object {
        private const val fileName = "settings.json"
        fun load(): CLISettings {
            return DataFileHandler.load(fileName, CLISettings())
        }
    }

    fun save() {
        DataFileHandler.save(fileName, this)
    }
}