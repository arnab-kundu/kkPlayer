package com.akundu.kkplayer.service.playback

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.akundu.kkplayer.Logg
import com.akundu.kkplayer.database.entity.SongEntity
import com.akundu.kkplayer.getDrawable
import com.akundu.kkplayer.storage.Constants.MEDIA_PATH
import java.io.File

object AlbumArtExtractor {
    const val DEFAULT_MAX_SIZE = 512

    fun extractAlbumArt(
        context: Context,
        songEntity: SongEntity,
        retriever: MediaMetadataRetriever = MediaMetadataRetriever(),
    ): Bitmap =
        try {
            if (songEntity.isDownloaded) {
                retriever.setDataSource(File("$MEDIA_PATH/${songEntity.fileName}").absolutePath)
            } else {
                retriever.setDataSource(songEntity.url, HashMap<String, String>())
            }
            val data = retriever.embeddedPicture ?: throw RuntimeException("No embedded album art")
            decodeAndScale(data)
        } catch (e: Exception) {
            Logg.d("No embedded album art for ${songEntity.fileName}: ${e.message}")
            ContextCompat.getDrawable(context, getDrawable(songEntity.movie))!!.toBitmap()
        } finally {
            retriever.release()
        }

    fun decodeAndScale(
        data: ByteArray,
        maxSize: Int = DEFAULT_MAX_SIZE,
    ): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(data, 0, data.size, bounds)
        var sampleSize = 1
        while (bounds.outWidth / sampleSize > maxSize || bounds.outHeight / sampleSize > maxSize) {
            sampleSize *= 2
        }
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        return BitmapFactory.decodeByteArray(data, 0, data.size, options)
    }
}
