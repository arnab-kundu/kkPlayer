package com.akundu.kkplayer.provider

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

/**
 * This class helps to give its own file access permission to other 3rd party Apps using ContentProvider.
 * - It needs context, the file & the package name of the 3rd party app.
 *
 * @param context
 * @param file
 * @param packageNameOfThirdPartyApp
 */
class FileAccessPermissionProvider(context: Context, file: File) {

    private val contentUri: Uri = FileProvider.getUriForFile(context, PACKAGE_NAME, file)
    private var context: Context

    init {
        this.context = context
    }

    fun grandUriPermissionsToThirdPartyApp(packageNameOfThirdPartyApp: String) {
        Log.d(TAG, "uriData: $contentUri")

        context.grantUriPermission(
            packageNameOfThirdPartyApp,
            contentUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    companion object {
        private const val TAG = "FileAccessPermissionPro"

        private const val PACKAGE_NAME = "com.akundu.kkplayer"


        // region 3rd party apps package name
        const val PACKAGE_NAME_OF_YOUTUBE_MUSIC = "com.google.android.apps.youtube.music"
        const val PACKAGE_NAME_OF_WYNK_MUSIC = "com.bsbportal.music"
        // endregion

    }
}