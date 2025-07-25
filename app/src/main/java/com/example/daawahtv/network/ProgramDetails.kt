package com.example.daawahtv.network

data class ProgramDetails(
    val id: Int,
    val title: RenderedText,
    val content: RenderedText,
    val better_featured_image: FeaturedImage?
)

// Note: This file uses 'RenderedText', which is different from 'RenderedTitle'
// and is fine to keep here as long as it's not duplicated elsewhere.
data class RenderedText(
    val rendered: String
)