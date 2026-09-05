package com.akundu.kkplayer.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class ApiRequestTest {
    private lateinit var apiRequest: ApiRequest

    @Before
    fun setUp() {
        // A stand-in base url keeps the assertions readable; the production one is covered below.
        apiRequest =
            Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .build()
                .create(ApiRequest::class.java)
    }

    @Test
    fun `downloadSong resolves the file name against the base url`() {
        val request = apiRequest.downloadSong("bhole_shankar.mp3").request()

        assertEquals("${BASE_URL}bhole_shankar.mp3", request.url.toString())
    }

    @Test
    fun `downloadSong issues a bodyless GET`() {
        val request = apiRequest.downloadSong("bhole_shankar.mp3").request()

        assertEquals("GET", request.method)
        assertNull(request.body)
    }

    @Test
    fun `downloadSong percent encodes a file name containing spaces`() {
        val request = apiRequest.downloadSong("bhole shankar.mp3").request()

        assertEquals("${BASE_URL}bhole%20shankar.mp3", request.url.toString())
    }

    @Test
    fun `downloadSong keeps a file name to a single path segment`() {
        // The separator is escaped, so a file name can never climb into another path.
        val request = apiRequest.downloadSong("nested/bhole_shankar.mp3").request()

        assertEquals("${BASE_URL}nested%2Fbhole_shankar.mp3", request.url.toString())
    }

    @Test
    fun `downloadSongByUrl uses an absolute url as given`() {
        val songUrl = "https://cs1.mp3.pm/download/1312416/hanuman_chalisa.mp3"

        val request = apiRequest.downloadSongByUrl(songUrl).request()

        assertEquals(songUrl, request.url.toString())
    }

    @Test
    fun `downloadSongByUrl resolves a relative url against the base url`() {
        val request = apiRequest.downloadSongByUrl("hanuman_chalisa.mp3").request()

        assertEquals("${BASE_URL}hanuman_chalisa.mp3", request.url.toString())
    }

    @Test
    fun `downloadSongByUrl issues a bodyless GET`() {
        val request = apiRequest.downloadSongByUrl("https://cs1.mp3.pm/hanuman_chalisa.mp3").request()

        assertEquals("GET", request.method)
        assertNull(request.body)
    }

    @Test
    fun `each call builds its own request`() {
        val first = apiRequest.downloadSong("first.mp3")
        val second = apiRequest.downloadSong("second.mp3")

        assertEquals("${BASE_URL}first.mp3", first.request().url.toString())
        assertEquals("${BASE_URL}second.mp3", second.request().url.toString())
    }

    @Test
    fun `the shared retrofit instance downloads from the configured host`() {
        val production = RetrofitRequest.getRetrofitInstance().create(ApiRequest::class.java)

        val request = production.downloadSong("bhole_shankar.mp3").request()

        assertEquals("https://pwdown.info/11981/bhole_shankar.mp3", request.url.toString())
    }

    private companion object {
        const val BASE_URL = "https://example.com/songs/"
    }
}
