package com.example.daawahtv.network

data class TvShowDetailsResponse(
    val status: Boolean,
    val message: String,
    val data: TvShowData
)

data class TvShowData(
    val details: TvShowDetails
)

data class TvShowDetails(
    val id: Long,
    val title: String,
    val image: String,
    val post_type: String,
    val description: String,
    val genre: List<String>,
    val casts: List<Cast>,
    val seasons: Seasons
)

data class Cast(
    val id: String,
    val image: String,
    val name: String
)

data class Seasons(
    val count: Int,
    val data: List<Season>
)

data class Season(
    val id: Int,
    val name: String
)
data class EpisodeDetailsApiResponse(
    val status: Boolean,
    val data: EpisodeData
)

data class EpisodeData(
    val url_link: String
)
