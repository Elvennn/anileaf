package io.elven.settings

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

object DataFileHandler {
    val defaultBasePath: String = "${System.getProperty("user.home")}/.config/anileaf"
    val mapper = jacksonObjectMapper()

    init {
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    inline fun <reified T : Any> load(fileName: String, default: T, basePathOverride: String? = null): T {
        val basePath = basePathOverride ?: defaultBasePath
        createIfNeeded(basePath)
        val file = File("$basePath/$fileName")
        if (!file.exists()) {
            save(fileName, default, basePathOverride)
        }
        return mapper.readValue(file.readText())
    }

    fun save(fileName: String, data: Any, basePathOverride: String? = null) {
        val basePath = basePathOverride ?: defaultBasePath
        createIfNeeded(basePath)
        File("$basePath/$fileName").writeText(mapper.writeValueAsString(data))
    }

    fun createIfNeeded(basePath: String) {
        if (!File(basePath).exists()) {
            File(basePath).mkdir()
        }
    }
}