package com.akundu.kkplayer.feature.main.view

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akundu.kkplayer.KkPlayerApp
import com.akundu.kkplayer.Logg
import com.akundu.kkplayer.R
import com.akundu.kkplayer.data.SongDataProvider.kkSongList
import com.akundu.kkplayer.database.SongDatabase
import com.akundu.kkplayer.database.dao.SongDao
import com.akundu.kkplayer.database.entity.SongEntity
import com.akundu.kkplayer.feature.main.ui.SongItem
import com.akundu.kkplayer.feature.main.viewModel.MainViewModel
import com.akundu.kkplayer.permission.RuntimePermission.askNotificationPermission
import com.akundu.kkplayer.presentation.viewModelFactory
import com.akundu.kkplayer.ui.theme.KkPlayerTheme
import es.dmoral.toasty.Toasty


class MainActivity : ComponentActivity() {

    lateinit var database: SongDatabase
    private lateinit var dao: SongDao
    private lateinit var songList: List<SongEntity>
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
                // Surface(color = Color.Gray) { SongListPreview(songListState) } // Surface Not using anymore

                // A Scaffold container helps to enable edgeToEdge support.
                // innerPadding parameter helps to give some extra padding to the content of LazyColumn in topBar and bottomBar section. However when scroll it takes the whole screen.
                Scaffold { innerPadding ->
                    Image(modifier = Modifier.blur(16.dp), painter = painterResource(id = R.drawable.background), contentDescription = null, contentScale = ContentScale.FillBounds)
                    LazyColumn(contentPadding = innerPadding) {
                        songListState.value?.let { itemsIndexed(it) { _, song -> SongItem(song = song) } }
                    }
                }
            }
        }

        // ActivityCompat.requestPermissions(this@MainActivity, arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE), 111)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            askNotificationPermission(this, requestPermissionLauncher)
        }
    }

    @Suppress("RemoveExplicitTypeArguments")
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
        // Toasty.success(context, "Online!", Toast.LENGTH_SHORT).show()
    }

    fun noInternet(context: Context) {
        Toasty.error(context, "No Internet!", Toast.LENGTH_SHORT).show()
    }
}