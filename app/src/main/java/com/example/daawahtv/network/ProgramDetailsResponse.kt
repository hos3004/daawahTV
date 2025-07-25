package com.example.daawahtv.network

data class ProgramDetailsResponse(
    val id: Long,
    val title: RenderedField,
    val content: RenderedField,
    val better_featured_image: FeaturedImage?
)

data class RenderedField(
    val rendered: String
)