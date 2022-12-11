package com.akundu.kkplayer.storage

import com.akundu.kkplayer.BuildConfig

object Constants {

    /** App directory file path */
    // const val MEDIA_PATH = "/storage/emulated/0/Android/data/com.akundu.kkplayer/files/"

    /** Scoped storage file path */
    const val INTERNAL_MEDIA_PATH = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/"

    /**
     * Paths of Shared public directory of
     * music, pictures, videos, downloads folders
     */
    const val MUSIC_PATH = "/storage/emulated/0/Music/"
    const val PICTURES_PATH = "/storage/emulated/0/Pictures/"
    const val VIDEOS_PATH = "/storage/emulated/0/Videos/"
    const val DOWNLOAD_PATH = "/storage/emulated/0/Download/"
}