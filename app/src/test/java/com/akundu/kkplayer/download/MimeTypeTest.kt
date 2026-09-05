package com.akundu.kkplayer.download

import org.junit.Assert.assertEquals
import org.junit.Test

class MimeTypeTest {
    @Test
    fun `covers every supported download type`() {
        assertEquals(
            listOf("AUDIO", "DOCUMENT", "IMAGE", "PDF", "VIDEO", "ZIP"),
            MimeType.entries.map { it.name },
        )
    }

    @Test
    fun `entries are resolvable by name`() {
        assertEquals(MimeType.AUDIO, MimeType.valueOf("AUDIO"))
        assertEquals(MimeType.ZIP, MimeType.valueOf("ZIP"))
    }
}
