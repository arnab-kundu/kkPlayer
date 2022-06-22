package com.akundu.kkplayer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever

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
}