package com.example.daawahtv

import org.junit.Assert.assertEquals
import org.junit.Test

class YouTubePlayerActivityTest {

    private fun extract(url: String): String? {
        val activity = YouTubePlayerActivity()
        val method = YouTubePlayerActivity::class.java.getDeclaredMethod("extractYouTubeId", String::class.java)
        method.isAccessible = true
        return method.invoke(activity, url) as String?
    }

    @Test
    fun extract_fromWatchUrl() {
        val id = extract("https://www.youtube.com/watch?v=abc123")
        assertEquals("abc123", id)
    }

    @Test
    fun extract_fromShortUrl() {
        val id = extract("https://youtu.be/abc123")
        assertEquals("abc123", id)
    }

    @Test
    fun extract_fromEmbedUrl() {
        val id = extract("https://www.youtube.com/embed/abc123")
        assertEquals("abc123", id)
    }
}
