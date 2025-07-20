package com.akundu.kkplayer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.*

object MetaDataExtractor {

    fun extractMp3(path: String): Bitmap? {

        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(path)

        val data = mmr.embeddedPicture
        if (data != null) {
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            return bitmap
        }
        return null
    }

    fun extractMediaInfo(path: String): String {
        var mediaInfoFormattedText = ""

        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(path)
        //mmr.extractMetadata(METADATA_KEY_TITLE)?.let { mediaInfoFormattedText +=    "TITLE      : $it\n" }
        mmr.extractMetadata(METADATA_KEY_ARTIST)?.let { mediaInfoFormattedText +=   "ARTIST     : $it\n" }
        mmr.extractMetadata(METADATA_KEY_ALBUM)?.let { mediaInfoFormattedText +=    "ALBUM      : $it. " }
        //mmr.extractMetadata(METADATA_KEY_AUTHOR)?.let { mediaInfoFormattedText +=   "AUTHOR     : $it\n" }
        //mmr.extractMetadata(METADATA_KEY_COMPOSER)?.let { mediaInfoFormattedText += "COMPOSER   : $it\n" }
        mmr.extractMetadata(METADATA_KEY_YEAR)?.let { mediaInfoFormattedText +=     "YEAR       : $it\n" }
        mmr.extractMetadata(METADATA_KEY_ALBUMARTIST)?.let { mediaInfoFormattedText += "Album artist: $it\n" }

        return mediaInfoFormattedText
    }
}
