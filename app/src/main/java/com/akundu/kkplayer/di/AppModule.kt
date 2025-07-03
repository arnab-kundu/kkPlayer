package com.akundu.kkplayer.di

import android.content.Context
import com.akundu.kkplayer.database.SongDatabase
import com.akundu.kkplayer.domain.Repository
import com.akundu.kkplayer.domain.RepositoryImpl
import com.akundu.kkplayer.network.ApiRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

interface AppModule {
    val api: ApiRequest
    val database: SongDatabase
    val repository: Repository
}

class AppModuleImpl(private val appContext: Context) : AppModule {
    override val api: ApiRequest by lazy {
        Retrofit.Builder()
            .baseUrl("https://my-url.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }

    override val database: SongDatabase by lazy {
        SongDatabase.getDatabase(appContext)
    }

    override val repository: Repository by lazy {
        RepositoryImpl(api, database)
    }
}
