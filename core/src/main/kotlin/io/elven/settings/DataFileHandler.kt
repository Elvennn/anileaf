package io.elven.settings

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

object DataFileHandler {
    val basePath: String = "${System.getProperty("user.home")}/.config/anileaf"
    val mapper = jacksonObjectMapper()

    init {
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        if (!File(basePath).exists()) {
            File(basePath).mkdir()
        }
    }

    inline fun <reified T : Any> load(fileName: String, default: T) : T {
        val file = File("$basePath/$fileName")
        if (!file.exists()) {
            save(fileName, default)
        }
        return mapper.readValue(file.readText())
    }

    fun save(fileName: String, data: Any) {
        File("$basePath/$fileName").writeText(mapper.writeValueAsString(data))
    }
}