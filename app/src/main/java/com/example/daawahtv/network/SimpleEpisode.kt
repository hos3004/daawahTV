package com.example.daawahtv.network

// Now uses RenderedTitle from Common.kt
data class SimpleEpisode(
    val id: Long,
    val title: RenderedTitle,
    val duration: String?,
    val image: String?
)