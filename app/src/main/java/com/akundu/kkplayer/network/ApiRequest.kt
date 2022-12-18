package com.akundu.kkplayer.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface ApiRequest {

    @GET("{fileName}")
    fun downloadSong(@Path("fileName") fileName: String): Call<ResponseBody>

    @GET
    fun downloadSongByUrl(@Url url: String): Call<ResponseBody>
}