package com.akundu.kkplayer

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequest.Builder
import androidx.work.WorkManager
import com.akundu.kkplayer.data.Song
import com.akundu.kkplayer.data.SongDataProvider.kkSongList
import com.akundu.kkplayer.database.SongDatabase
import com.akundu.kkplayer.database.dao.SongDao
import com.akundu.kkplayer.database.entity.SongEntity
import com.akundu.kkplayer.download.AndroidDownloader
import com.akundu.kkplayer.feature.main.viewModel.MainViewModel
import com.akundu.kkplayer.permission.RuntimePermission.askNotificationPermission
import com.akundu.kkplayer.service.BackgroundSoundService
import com.akundu.kkplayer.service.ServiceTools
import com.akundu.kkplayer.storage.Constants.INTERNAL_MEDIA_PATH
import com.akundu.kkplayer.ui.theme.Blue
import com.akundu.kkplayer.ui.theme.KkPlayerTheme
import com.akundu.kkplayer.work.DownloadWork
import com.google.android.material.snackbar.Snackbar
import com.plcoding.manualdependencyinjection.presentation.viewModelFactory
import es.dmoral.toasty.Toasty
import java.io.File
import java.io.FileNotFoundException


class MainActivity : ComponentActivity() {

    lateinit var database: SongDatabase
    private lateinit var dao: SongDao
    private lateinit var songList: List<SongEntity>
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = SongDatabase.getDatabase(this)
        dao = database.songDao()
        if (dao.getTotalCount() == 0) {
            var songEntity: SongEntity
            kkSongList.forEachIndexed { _, song ->
                songEntity = SongEntity(
                    title = song.title,
                    artist = song.artist,
                    fileName = song.fileName,
                    url = song.url,
                    movie = song.movie
                )
                dao.addSong(songEntity)
            }
        }
        songList = database.songDao().getAllSongs()


        setContent {
            KkPlayerTheme {
                viewModel = viewModel<MainViewModel>(
                    factory = viewModelFactory {
                        MainViewModel(KkPlayerApp.appModule.repository)
                    }
                )
                viewModel.allSongsLiveData()
                val songListState = viewModel.songList.observeAsState(null)
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    SongListPreview(songListState)
                }
            }
        }

        // ActivityCompat.requestPermissions(this@MainActivity, arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE), 111)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            askNotificationPermission(this, requestPermissionLauncher)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult<String, Boolean>(RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Logg.i("Callback: Permission is granted")
            // Permission is granted. Continue the action or workflow in your app.
        } else {
            Logg.e("Callback: Permission is NOT granted")
            // Explain to the user that the feature is unavailable because the
            // feature requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
        }
    }

    fun internetAvailable(context: Context) {
        Toasty.success(context, "Online!", Snackbar.LENGTH_SHORT).show()
    }

    fun noInternet(context: Context) {
        Toasty.error(context, "No Internet!", Snackbar.LENGTH_SHORT).show()
    }


    @Composable
    fun SongItem(song: Song, index: Int) {
        val context = LocalContext.current
        Row(
            modifier = Modifier
                .padding(horizontal = 6.dp, vertical = 3.dp)
                .background(MaterialTheme.colors.background)
                .clickable { playSong(context, song.title, song.fileName, index) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painterResource(id = getDrawable(song.movie)),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(88.dp)
                    // .clip(CircleShape)
                    .padding(12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = song.title, style = MaterialTheme.typography.h6, color = Color.DarkGray)
                Row {
                    Text(
                        stringResource(id = R.string.artist),
                        style = MaterialTheme.typography.body2,
                        color = Color.DarkGray
                    )
                    Text(
                        text = song.artist, style = MaterialTheme.typography.body2,
                        color = Color.DarkGray
                    )
                }
            }
            IconButton(
                onClick = {
                    val downloadUsingAndroidDownloaderAPI = false
                    if (!downloadUsingAndroidDownloaderAPI) {
                        if (isFileExists(fileName = song.fileName)) playSong(context, song.title, song.fileName, index) else download(
                            context,
                            0L,
                            song.fileName,
                            song.url,
                            song.movie
                        )
                    } else {
                        if (isFileExists(fileName = song.fileName)) playSong(context, song.title, song.fileName, index) else downloadUsingAndroidDownloadManagerAPI(
                            context,
                            song.fileName,
                            song.url,
                            song.movie
                        )
                    }
                }
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isFileExists(fileName = song.fileName)) android.R.drawable.ic_media_play else {
                            R.drawable.ic_download
                        }
                    ),
                    contentDescription = if (isFileExists(fileName = song.fileName)) "Play" else {
                        "Download"
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .padding(8.dp),
                    tint = if (isFileExists(fileName = song.fileName)) Color(0xFF0C610C) else {
                        Blue
                    }
                )
            }
        }
    }


    @Composable
    fun SongItem(song: SongEntity) {
        val context = LocalContext.current
        Row(
            modifier = Modifier
                .semantics { contentDescription = "songItem" }
                .background(MaterialTheme.colors.background)
                .clickable { playSong(context, song.title, song.fileName, song.id.toInt()) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painterResource(id = getDrawable(song.movie)),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(88.dp)
                    // .clip(CircleShape)
                    .padding(12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = song.title, style = MaterialTheme.typography.h6, color = Color.DarkGray)
                Row {
                    Text(
                        stringResource(id = R.string.artist),
                        style = MaterialTheme.typography.body2,
                        color = Color.DarkGray
                    )
                    Text(
                        text = song.artist, style = MaterialTheme.typography.body2,
                        color = Color.DarkGray
                    )
                }
            }
            IconButton(
                onClick = {
                    val downloadUsingAndroidDownloaderAPI = false
                    if (!downloadUsingAndroidDownloaderAPI) {
                        if (isFileExists(fileName = song.fileName)) playSong(context, song.title, song.fileName, song.id.toInt()) else download(
                            context,
                            song.id,
                            song.fileName,
                            song.url,
                            song.movie
                        )
                    } else {
                        if (isFileExists(fileName = song.fileName)) playSong(context, song.title, song.fileName, song.id.toInt()) else downloadUsingAndroidDownloadManagerAPI(
                            context,
                            song.fileName,
                            song.url,
                            song.movie
                        )
                    }
                    //TODO
                    //viewModel.updateSongDownloadStatus(song.id)
                }
            ) {
                Icon(
                    painter = painterResource(id = if (song.isDownloaded) android.R.drawable.ic_media_play else R.drawable.ic_download),
                    contentDescription = if (song.isDownloaded) "Play" else "Download",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .padding(8.dp),
                    tint = if (song.isDownloaded) Color(0xFF0C610C) else Blue
                )
            }
        }
    }

    private fun isFileExists(fileName: String): Boolean {
        val uriString: String = File("$INTERNAL_MEDIA_PATH$fileName").toString()
        val songFile = File(uriString)
        val isFileExist = songFile.exists()
        if (isFileExist) {
            Logg.i("Is file exists: $isFileExist. Filename: $fileName")
        } else {
            Logg.e("Is file exists: $isFileExist. Filename: $fileName")
        }
        return isFileExist
    }

    private fun playSong(context: Context, title: String, fileName: String, index: Int) {

        Logg.i("FileName: $fileName")

        try {

            val uriString: String = File("$INTERNAL_MEDIA_PATH$fileName").toString()

            if (isFileExists(fileName = fileName)) {

                if (ServiceTools.isServiceRunning(context = context, "com.akundu.kkplayer.service.BackgroundSoundService")) {
                    context.stopService(Intent(context, BackgroundSoundService::class.java))
                }

                val svc = Intent(context, BackgroundSoundService::class.java)
                svc.putExtra("uri", uriString)
                svc.putExtra("songTitle", title)
                svc.putExtra("id", index)
                context.startService(svc)

                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra("index", index)
                // context.startActivity(intent)

            } else {
                Logg.e("File exist: false")
                Logg.e("UriString: $uriString")

                Toast.makeText(context, "Please download", Toast.LENGTH_SHORT).show()
            }

        } catch (e: FileNotFoundException) {
            Logg.e("$fileName not found. Cause ${e.localizedMessage}")
            Toast.makeText(context, "Please download", Toast.LENGTH_SHORT).show()

        } catch (e: NullPointerException) {
            Logg.e("Cause ${e.localizedMessage}")
            Toast.makeText(context, "Please download", Toast.LENGTH_SHORT).show()
        }
    }


    fun download(context: Context, id: Long, fileName: String, url: String, movie: String) {
        Logg.i("Downloading: $fileName")

        val notificationID = System.currentTimeMillis().toInt()

        val data: Data = Data.Builder()
            .putLong("id", id)
            .putString("fileName", fileName)
            .putString("url", url)
            .putString("movie", movie)
            .putInt("notificationID", notificationID)
            .build()

        val constraints: Constraints = Constraints.Builder()
            // .setRequiredNetworkType(NetworkType.UNMETERED)
            // .setRequiresBatteryNotLow(true)
            // .setRequiresStorageNotLow(true)
            .build()

        val request = Builder(DownloadWork::class.java)
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(request)

        AppsNotificationManager.getInstance(context)?.downloadingNotification(
            targetNotificationActivity = MainActivity::class.java,
            channelId = "CHANNEL_ID",
            title = fileName,
            text = "Downloading",
            bigText = "",
            notificationId = notificationID,
            drawableId = getDrawable(movie)
        )
    }

    /**
     * Download file using new - **Android DownloadManager API**.
     *
     *
     *  - Downloaded file will be saved in **Public Downloads Folder**
     *  - This API have build-in feature for showing notifications of in-progress-downloads and download-completion
     */
    @Suppress("UNUSED_PARAMETER")
    private fun downloadUsingAndroidDownloadManagerAPI(context: Context, fileName: String, url: String, movie: String) {
        val downloader = AndroidDownloader(context = context)
        downloader.downloadFile(
            url = url,
            fileName = fileName
        )
    }

    @Preview
    @Composable
    fun SongItemPreview() {
        SongItem(kkSongList[0], 0)
    }


    @Composable
    fun SongListCompose1(
        songList: List<Song>,
        modifier: Modifier = Modifier
    ) {
        // Use LazyRow when making horizontal lists
        LazyColumn(modifier = modifier) {
            itemsIndexed(songList) { index, song ->
                SongItem(song = song, index = index)
            }
        }
    }


    @Composable
    fun SongListCompose(
        songList: List<SongEntity>,
        modifier: Modifier = Modifier.semantics { contentDescription = "songsList" }
    ) {
        LazyColumn(modifier = modifier) {
            itemsIndexed(songList) { _, song ->
                SongItem(song = song)
            }
        }
    }


    //@Preview
    @Composable
    fun SongListPreview(songList: State<List<SongEntity>?>) {
        //SongListCompose(SongDataProvider.kkSongList)
        songList.value?.let { SongListCompose(songList = it) }
    }


    private fun getDrawable(movie: String): Int {
        return when (movie) {
            "Bajrangi Bhaijaan" -> R.drawable.bajrangi_bhaijaan
            "Bhool Bhulaiyaa" -> R.drawable.bhool_bhulaiyaa
            "Crook" -> R.drawable.crook
            "EK THA TIGER" -> R.drawable.ek_tha_tiger
            "Force" -> R.drawable.force
            "G" -> R.drawable.g
            "Gangster" -> R.drawable.gangster
            "Golmaal Returns" -> R.drawable.golmaal_returns
            "Jannat" -> R.drawable.jannat
            "Jism" -> R.drawable.jism
            "Kabir Singh (2019)" -> R.drawable.kabir_singh
            "Kites" -> R.drawable.kites
            "Laali Ki Shaadi" -> R.drawable.laali_ki_shaadi
            "Musafir" -> R.drawable.musafir
            "New York" -> R.drawable.new_york
            "Om Shanti Om" -> R.drawable.om_shanti_om
            "Raaz Reboot" -> R.drawable.raaz_reboot
            "Race" -> R.drawable.race
            "Raees" -> R.drawable.raees
            "raqeeb" -> R.drawable.raqeeb
            "Raaz - TMC" -> R.drawable.razz
            "Razz" -> R.drawable.razz
            "Saathiya" -> R.drawable.saathiya
            "The Killer" -> R.drawable.the_killer
            "Life...Metro" -> R.drawable.life_metro
            "Live-The Train" -> R.drawable.the_train
            "Woh Lamhe" -> R.drawable.woh_lamhe
            "Zeher" -> R.drawable.zeher
            else -> R.drawable.ic_music
        }
    }

}