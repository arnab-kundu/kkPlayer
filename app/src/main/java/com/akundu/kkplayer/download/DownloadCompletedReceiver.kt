package com.akundu.kkplayer.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DownloadCompletedReceiver : BroadcastReceiver() {
    private lateinit var downloadManager: DownloadManager

    override fun onReceive(context: Context?, intent: Intent?) {
        downloadManager = context?.getSystemService(DownloadManager::class.java)!!
        // Check if the download is complete
        if (intent?.action == "android.intent.action.DOWNLOAD_COMPLETE") {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if (id != -1L) {
                Log.i(TAG, "onReceive: Download with id $id finished!")

                val query = DownloadManager.Query().setFilterById(id)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val columIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    if (columIndex >= 0) {
                        val status = cursor.getInt(columIndex)

                        // TODO Update UI or Handle Error
                        when (status) {
                            DownloadManager.STATUS_SUCCESSFUL -> Log.i(TAG, "onReceive: Download with id $id successful")
                            DownloadManager.STATUS_FAILED     -> Log.e(TAG, "onReceive: Download with id $id failed")
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "DownloadCompletedReceiver"
    }
}
