package com.akundu.kkplayer.media

import android.media.MediaMetadataRetriever
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowMediaMetadataRetriever

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class MetaDataExtractorTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @After
    fun tearDown() {
        ShadowMediaMetadataRetriever.reset()
    }

    private fun trackWithMetadata(vararg metadata: Pair<Int, String>): String {
        val path = temporaryFolder.newFile("track.mp3").absolutePath
        metadata.forEach { (key, value) -> ShadowMediaMetadataRetriever.addMetadata(path, key, value) }
        return path
    }

    @Test
    fun `formats the artist album year and album artist`() {
        val path =
            trackWithMetadata(
                MediaMetadataRetriever.METADATA_KEY_ARTIST to "KK",
                MediaMetadataRetriever.METADATA_KEY_ALBUM to "Humraaz",
                MediaMetadataRetriever.METADATA_KEY_YEAR to "2002",
                MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST to "Various Artists",
            )

        val info = MetaDataExtractor.extractMediaInfo(path)

        assertTrue(info.contains("ARTIST     : KK"))
        assertTrue(info.contains("ALBUM      : Humraaz"))
        assertTrue(info.contains("YEAR       : 2002"))
        assertTrue(info.contains("Album artist: Various Artists"))
    }

    @Test
    fun `omits metadata the track does not carry`() {
        val path = trackWithMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST to "KK")

        val info = MetaDataExtractor.extractMediaInfo(path)

        assertEquals("ARTIST     : KK\n", info)
    }

    @Test
    fun `returns an empty description for a track without metadata`() {
        val path = trackWithMetadata()

        assertEquals("", MetaDataExtractor.extractMediaInfo(path))
    }

    @Test
    fun `returns no artwork when the track has none embedded`() {
        val path = trackWithMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST to "KK")

        assertNull(MetaDataExtractor.extractMp3(path))
    }
}
