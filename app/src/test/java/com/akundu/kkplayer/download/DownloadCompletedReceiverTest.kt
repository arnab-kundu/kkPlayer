package com.akundu.kkplayer.download

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class DownloadCompletedReceiverTest {
    private val downloadManager: DownloadManager = mock()
    private val context: Context = mock()
    private val receiver = DownloadCompletedReceiver()

    @Before
    fun setUp() {
        whenever(context.getSystemService(DownloadManager::class.java)).thenReturn(downloadManager)
    }

    private fun completionIntent(downloadId: Long): Intent {
        val intent: Intent = mock()
        whenever(intent.action).thenReturn("android.intent.action.DOWNLOAD_COMPLETE")
        whenever(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)).thenReturn(downloadId)
        return intent
    }

    private fun cursorReturning(
        status: Int,
        columnIndex: Int = 0,
        movesToFirst: Boolean = true,
    ): Cursor {
        val cursor: Cursor = mock()
        whenever(cursor.moveToFirst()).thenReturn(movesToFirst)
        whenever(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)).thenReturn(columnIndex)
        whenever(cursor.getInt(columnIndex)).thenReturn(status)
        return cursor
    }

    @Test
    fun `queries the download manager for a completed download`() {
        val cursor = cursorReturning(DownloadManager.STATUS_SUCCESSFUL)
        whenever(downloadManager.query(any())).thenReturn(cursor)

        receiver.onReceive(context, completionIntent(42L))

        verify(downloadManager).query(any())
        verify(cursor).getInt(0)
    }

    @Test
    fun `handles a failed download`() {
        val cursor = cursorReturning(DownloadManager.STATUS_FAILED)
        whenever(downloadManager.query(any())).thenReturn(cursor)

        receiver.onReceive(context, completionIntent(42L))

        verify(cursor).getInt(0)
    }

    @Test
    fun `ignores intents with a different action`() {
        val intent: Intent = mock()
        whenever(intent.action).thenReturn("android.intent.action.VIEW")

        receiver.onReceive(context, intent)

        verify(downloadManager, never()).query(any())
    }

    @Test
    fun `ignores an intent without a download id`() {
        receiver.onReceive(context, completionIntent(-1L))

        verify(downloadManager, never()).query(any())
    }

    @Test
    fun `tolerates an empty cursor`() {
        val cursor = cursorReturning(0, movesToFirst = false)
        whenever(downloadManager.query(any())).thenReturn(cursor)

        receiver.onReceive(context, completionIntent(42L))

        verify(cursor, never()).getInt(any())
    }

    @Test
    fun `tolerates a missing status column`() {
        val cursor = cursorReturning(0, columnIndex = -1)
        whenever(downloadManager.query(any())).thenReturn(cursor)

        receiver.onReceive(context, completionIntent(42L))

        verify(cursor, never()).getInt(any())
    }

    @Test
    fun `ignores a null intent`() {
        receiver.onReceive(context, null)

        verify(downloadManager, never()).query(any())
    }
}
