package com.akundu.kkplayer.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Test

class RetrofitRequestTest {
    @Test
    fun `provides a retrofit instance`() {
        assertNotNull(RetrofitRequest.getRetrofitInstance())
    }

    @Test
    fun `reuses the same retrofit instance`() {
        assertSame(RetrofitRequest.getRetrofitInstance(), RetrofitRequest.getRetrofitInstance())
    }

    @Test
    fun `is configured with the download base url`() {
        assertEquals("https://pwdown.info/11981/", RetrofitRequest.getRetrofitInstance().baseUrl().toString())
    }

    @Test
    fun `can create the api request interface`() {
        assertNotNull(RetrofitRequest.getRetrofitInstance().create(ApiRequest::class.java))
    }
}
