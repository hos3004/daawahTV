package com.example.daawahtv.network

data class HomeResponse(
    val status: Boolean,
    val message: String,
    val data: HomeData
)

data class HomeData(
    val banner: List<BannerItem>?,
    val sliders: List<ContentSlider>?
)

data class BannerItem(
    val id: Int,
    val title: String,
    val image: String,
    val post_type: String
)

data class ContentSlider(
    val title: String,
    val view_all: Boolean,
    val type: String,
    val data: List<ProgramItem>
)

data class ProgramItem(
    val id: Int,
    val title: String,
    val image: String,
    val post_type: String
)
