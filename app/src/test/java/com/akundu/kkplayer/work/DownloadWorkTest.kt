package com.akundu.kkplayer.work

import android.content.Context
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.akundu.kkplayer.network.ApiRequest
import com.akundu.kkplayer.storage.AppFileManager
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Response
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class DownloadWorkTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val apiRequest: ApiRequest = mock()
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
    }

    private fun inputData(): Data =
        Data
            .Builder()
            .putLong("id", 1L)
            .putString("fileName", "song.mp3")
            .putString("url", "https://example.com/song.mp3")
            .putString("movie", "Jannat")
            .putInt("notificationID", 55)
            .build()

    private fun worker(): DownloadWork =
        TestListenableWorkerBuilder<DownloadWork>(context)
            .setInputData(inputData())
            .setWorkerFactory(
                object : WorkerFactory() {
                    override fun createWorker(
                        appContext: Context,
                        workerClassName: String,
                        workerParameters: WorkerParameters,
                    ): ListenableWorker =
                        DownloadWork(
                            appContext,
                            workerParameters,
                            apiRequest,
                            AppFileManager(externalStorageRoot = temporaryFolder.root),
                        )
                },
            ).build()

    private fun call(response: Response<ResponseBody>): Call<ResponseBody> {
        val call: Call<ResponseBody> = mock()
        whenever(call.execute()).thenReturn(response)
        return call
    }

    @Test
    fun `reports success for a successful download`() =
        runTest {
            val successful = call(Response.success("audio-bytes".toResponseBody("audio/mpeg".toMediaType())))
            whenever(apiRequest.downloadSongByUrl(any())).thenReturn(successful)

            assertEquals(ListenableWorker.Result.success(), worker().doWork())
        }

    @Test
    fun `saves the downloaded song to storage`() =
        runTest {
            val successful = call(Response.success("audio-bytes".toResponseBody("audio/mpeg".toMediaType())))
            whenever(apiRequest.downloadSongByUrl(any())).thenReturn(successful)

            worker().doWork()

            val downloaded = File(temporaryFolder.root, "Android/media/com.akundu.kkplayer/song.mp3")
            assertTrue(downloaded.exists())
            assertEquals("audio-bytes", downloaded.readText())
        }

    // A rejected response is worth another attempt later, so the work is retried rather than
    // reported as done.
    @Test
    fun `asks to retry when the server rejects the request`() =
        runTest {
            val failed = call(Response.error<ResponseBody>(404, "missing".toResponseBody("text/plain".toMediaType())))
            whenever(apiRequest.downloadSongByUrl(any())).thenReturn(failed)

            assertEquals(ListenableWorker.Result.retry(), worker().doWork())
        }

    @Test
    fun `reports failure when the request throws`() =
        runTest {
            val failing: Call<ResponseBody> = mock()
            whenever(failing.execute()).thenThrow(RuntimeException("offline"))
            whenever(apiRequest.downloadSongByUrl(any())).thenReturn(failing)

            assertEquals(ListenableWorker.Result.failure(), worker().doWork())
        }

    @Test
    fun `reads the download request from its input data`() =
        runTest {
            val worker = worker()

            assertEquals(1L, worker.inputData.getLong("id", 0L))
            assertEquals("song.mp3", worker.inputData.getString("fileName"))
            assertEquals("https://example.com/song.mp3", worker.inputData.getString("url"))
            assertEquals("Jannat", worker.inputData.getString("movie"))
            assertEquals(55, worker.inputData.getInt("notificationID", 0))
        }
}
