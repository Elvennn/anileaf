package io.elven.settings

import io.elven.anilist.AniEntry
import java.time.LocalDateTime
import java.time.ZoneOffset

class AnileafInternalData(private val basePathOverride: String? = null) {
    companion object {
        private const val dataFileName = "internal_data.json"
    }

    val data: InternalData

    init {
        data = DataFileHandler.load(dataFileName, InternalData(), basePathOverride)
    }

    fun save() {
        data.lastUpdate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        DataFileHandler.save(dataFileName, data, basePathOverride)
    }

    fun saveWithUpdatedAnime(aniEntry: AniEntry) {
        val indexToReplace = data.animeList.indexOfFirst { it.media == aniEntry.media }
        data.animeList[indexToReplace] = aniEntry
        save()
    }
}

data class InternalData(
    var lastUpdate: Long = 0,
    var animeList: Array<AniEntry> = emptyArray()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InternalData

        if (lastUpdate != other.lastUpdate) return false
        if (!animeList.contentEquals(other.animeList)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lastUpdate.hashCode()
        result = 31 * result + animeList.contentHashCode()
        return result
    }
}