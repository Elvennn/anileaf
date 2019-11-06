package io.elven.settings

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.elven.anilist.AniEntry
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

object AnileafInternalData {
    val data: InternalData
    val animeDownloadState: MutableMap<Int, MutableSet<Int>> = mutableMapOf()

    private const val dataFileName = "internal_data.json"
    private val mapper = jacksonObjectMapper()
    private val dataFile = File("${AnileafSettings.path}/$dataFileName")


    init {
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        if (!dataFile.exists()) {
            dataFile.writeText(mapper.writeValueAsString(InternalData()))
        }
        data = mapper.readValue(dataFile)
    }

    fun save() {
        data.lastUpdate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        dataFile.writeText(mapper.writeValueAsString(data))
    }
}

data class InternalData(
    var lastUpdate: Long = 0,
    var animeList: Array<AniEntry> = emptyArray()
)