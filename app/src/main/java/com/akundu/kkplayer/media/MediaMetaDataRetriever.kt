package com.akundu.kkplayer.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever // import wseemann.media.FFmpegMediaMetadataRetriever // alternative
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.akundu.kkplayer.Logg
import com.akundu.kkplayer.getDrawable
import com.akundu.kkplayer.storage.AppFileManager
import com.akundu.kkplayer.storage.Constants.MEDIA_PATH
import java.io.File
import java.io.FileOutputStream

/**
 * Using Ffmpeg libs FfmpegMediaMetadataRetriever
 */
@Suppress("RedundantExplicitType")
object MediaMetaDataRetriever {
    private const val THUMBNAILS_FOLDER_NAME = "thumbnails"

    /**
     * getMediaMetadataImageBitmap() function: Extract the media file metadata using Ffmpeg library.
     * Metadata can have many information, but here only fetching the song album image as bitmap.
     *
     * @param fileName String
     * @return albumArt: Bitmap?
     */
    fun getMediaImage(fileName: String): Bitmap? {
        val bitmap: Bitmap
        try {
            val uri: String = File("$MEDIA_PATH/$fileName").toString().toUri().toString()
            val retriever: MediaMetadataRetriever = MediaMetadataRetriever()
            retriever.setDataSource(uri)
            val data: ByteArray = retriever.embeddedPicture ?: throw RuntimeException("Data is null in MediaMetadataRetriever")

            // convert the byte array to a bitmap
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

            // do something with the image ...
            // mImageView.setImageBitmap(bitmap);
            retriever.release()
            return bitmap
        } catch (e: Exception) {
            Logg.e(e.message)
            return null
        }
    }

    /**
     * hasSavedMediaThumbnailsInCache function: saves media thumbnail into cache folder
     * @param context Context
     * @param fileName String
     * @param bitmap Bitmap
     * @return Boolean
     */
    fun hasSavedMediaThumbnailsInCache(
        context: Context,
        fileName: String,
        bitmap: Bitmap?,
    ): Boolean {
        if (fileName.isEmpty() || bitmap == null) {
            return false
        }

        // Create Folder
        AppFileManager().createFolder(THUMBNAILS_FOLDER_NAME, "${context.externalCacheDir}")

        // Create File
        val thumbnailImageFile: File = File("${context.externalCacheDir}/$THUMBNAILS_FOLDER_NAME/${fileName.replace(".mp3", ".jpg")}")
        thumbnailImageFile.createNewFile()

        // Save bitmap into file
        val fileOutputStream = FileOutputStream(thumbnailImageFile)
        bitmap.compress(JPEG, 10, fileOutputStream)

        // Verify saved file length
        if (thumbnailImageFile.length() > 0) {
            return true
        } else {
            // Delete file if length is Zero
            thumbnailImageFile.delete()
            return false
        }
    }

    /**
     * MediaMetaDataRetriever function: Extract the media file metadata using Ffmpeg library.
     * Metadata can have many information, but here only fetching the song album image as ImageBitmap.
     *
     * @param context Context
     * @param fileName String
     * @param movie String
     * @return albumArt: ImageBitmap
     */
    @Suppress("unused")
    fun mediaMetaDataRetriever(
        context: Context,
        fileName: String,
        movie: String,
    ): ImageBitmap {
        val bitmap = getMediaImage(fileName)
        if (bitmap != null) {
            return bitmap.asImageBitmap()
        } else {
            val icon: Bitmap = ContextCompat.getDrawable(context, getDrawable(movie))!!.toBitmap()
            return icon.asImageBitmap()
        }
    }

    /**
     * FetchMetadataFromCache() function: It fetch the saved image file in cache folder convert into bitmap and returns.
     * If file is not available returns a default avatar image from drawable.
     *
     * @param context Context
     * @param fileName String
     * @param movie String
     * @return albumArt: ImageBitmap
     */
    fun fetchMetadataFromCache(
        context: Context,
        fileName: String,
        movie: String,
    ): ImageBitmap {
        val thumbnailImageFile: File = File("${context.externalCacheDir}/$THUMBNAILS_FOLDER_NAME/${fileName.replace(".mp3", ".jpg")}")
        if (thumbnailImageFile.exists() && thumbnailImageFile.length() > 0) {
            val bitmap: Bitmap = BitmapFactory.decodeFile(thumbnailImageFile.absolutePath)
            return bitmap.asImageBitmap()
        } else {
            val icon: Bitmap = ContextCompat.getDrawable(context, getDrawable(movie))!!.toBitmap()
            return icon.asImageBitmap()
        }
    }
}
