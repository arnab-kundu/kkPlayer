package com.akundu.kkplayer

import android.util.Log
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class LoggTest {
    @Before
    fun setUp() {
        ShadowLog.clear()
    }

    private fun lastLog(): ShadowLog.LogItem = ShadowLog.getLogs().last()

    @Test
    fun `verbose logs are tinted purple`() {
        Logg.v("playing")

        assertEquals(Log.VERBOSE, lastLog().type)
        assertEquals("\uD83D\uDC9C playing", lastLog().msg)
    }

    @Test
    fun `debug logs are tinted blue`() {
        Logg.d("playing")

        assertEquals(Log.DEBUG, lastLog().type)
        assertEquals("\uD83D\uDC99 playing", lastLog().msg)
    }

    @Test
    fun `info logs are tinted green`() {
        Logg.i("playing")

        assertEquals(Log.INFO, lastLog().type)
        assertEquals("\uD83D\uDC9A playing", lastLog().msg)
    }

    @Test
    fun `warning logs are tinted yellow`() {
        Logg.w("nearly out of space")

        assertEquals(Log.WARN, lastLog().type)
        assertEquals("\uD83D\uDC9B nearly out of space", lastLog().msg)
    }

    @Test
    fun `error logs are tinted red`() {
        Logg.e("playback failed")

        assertEquals(Log.ERROR, lastLog().type)
        assertEquals("\uD83D\uDC94 playback failed", lastLog().msg)
    }

    @Test
    fun `a null message is logged rather than dropped`() {
        Logg.d(null)

        assertEquals("\uD83D\uDC99 null", lastLog().msg)
    }

    @Test
    fun `a warned throwable is logged by its message`() {
        Logg.w(Throwable("disk almost full"))

        assertEquals(Log.WARN, lastLog().type)
        assertEquals("\uD83D\uDC9B disk almost full", lastLog().msg)
    }

    @Test
    fun `a warned exception is logged by its message`() {
        Logg.w(IllegalStateException("no media player"))

        assertEquals(Log.WARN, lastLog().type)
        assertEquals("\uD83D\uDC9B no media player", lastLog().msg)
    }

    @Test
    fun `a warned linkage error is logged by its message`() {
        Logg.w(NoClassDefFoundError("missing codec"))

        assertEquals(Log.WARN, lastLog().type)
        assertEquals("\uD83D\uDC9B missing codec", lastLog().msg)
    }

    @Test
    fun `an errored throwable is logged by its message`() {
        Logg.e(Throwable("playback failed"))

        assertEquals(Log.ERROR, lastLog().type)
        assertEquals("\uD83D\uDC94 playback failed", lastLog().msg)
    }

    @Test
    fun `an errored exception is logged by its message`() {
        Logg.e(java.lang.Exception("playback failed"))

        assertEquals(Log.ERROR, lastLog().type)
        assertEquals("\uD83D\uDC94 playback failed", lastLog().msg)
    }

    @Test
    fun `a null throwable is logged without a message`() {
        Logg.e(null as Throwable?)

        assertEquals("\uD83D\uDC94 null", lastLog().msg)
    }

    @Test
    fun `the tag points back at the call site`() {
        // Logg reads the fourth frame off the stack, so the tag names whoever called the
        // wrapper below rather than Logg itself.
        logFromAHelper()

        assertTrue(
            "unexpected tag: ${lastLog().tag}",
            Regex("""\(LoggTest\.kt:\d+\)""").matches(lastLog().tag),
        )
    }

    private fun logFromAHelper() = Logg.d("from a helper")

    @Test
    fun `every level tags the same call site the same way`() {
        val loggers =
            listOf<(String) -> Unit>(
                { Logg.v(it) },
                { Logg.d(it) },
                { Logg.i(it) },
                { Logg.w(it) },
                { Logg.e(it) },
            )

        val tags =
            loggers.map { log ->
                ShadowLog.clear()
                log("message")
                lastLog().tag
            }

        assertEquals(1, tags.distinct().size)
    }
}
