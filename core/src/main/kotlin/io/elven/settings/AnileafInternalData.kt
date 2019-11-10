package io.elven.settings

import io.elven.anilist.AniEntry
import java.time.LocalDateTime
import java.time.ZoneOffset

class AnileafInternalData {
    companion object {
        private const val dataFileName = "internal_data.json"
    }
    val data: InternalData



    init {
        data = DataFileHandler.load(dataFileName, InternalData())
    }

    fun save() {
        data.lastUpdate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        DataFileHandler.save(dataFileName, data)
    }

    fun saveWithUpdatedAnime(aniEntry: AniEntry) {
        val indexToReplace = data.animeList.indexOfFirst { it.media == aniEntry.media }
        data.animeList[indexToReplace] = aniEntry
        save()
    }
}

data class InternalData(
    var lastUpdate: Long = 0,
    var animeList: Array<AniEntry> = emptyArray(),
    val animeDownloadState: MutableMap<Int, MutableSet<Int>> = mutableMapOf()

)