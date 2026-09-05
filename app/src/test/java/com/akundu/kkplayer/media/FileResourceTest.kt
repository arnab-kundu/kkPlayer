package com.akundu.kkplayer.media

import org.junit.Assert.assertEquals
import org.junit.Test

class FileResourceTest {
    @Test
    fun `maps every media store value to its file type`() {
        assertEquals(FileType.NONE, FileType.getEnum(0))
        assertEquals(FileType.IMAGE, FileType.getEnum(1))
        assertEquals(FileType.AUDIO, FileType.getEnum(2))
        assertEquals(FileType.VIDEO, FileType.getEnum(3))
        assertEquals(FileType.PLAYLIST, FileType.getEnum(4))
        assertEquals(FileType.SUBTITLE, FileType.getEnum(5))
        assertEquals(FileType.DOCUMENT, FileType.getEnum(6))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects an unknown media store value`() {
        FileType.getEnum(99)
    }
}
