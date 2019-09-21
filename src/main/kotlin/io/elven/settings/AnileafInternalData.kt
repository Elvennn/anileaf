package io.elven.settings

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.elven.anilist.AniEntry
import java.io.File
import java.time.LocalDate

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
        dataFile.writeText(mapper.writeValueAsString(data))
    }
}

data class InternalData(
    var lastUpdate: Long = LocalDate.now().toEpochDay(),
    var animeList: Array<AniEntry> = emptyArray()
)