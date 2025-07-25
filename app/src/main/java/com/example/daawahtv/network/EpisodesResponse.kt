package com.example.daawahtv.network

// Now uses RenderedTitle and FeaturedImage from Common.kt
data class Episode(
    val id: Int,
    val title: RenderedTitle,
    val acf: EpisodeACF,
    val better_featured_image: FeaturedImage?
)

data class EpisodeACF(
    val video_url: String?,
    val duration: String?
)