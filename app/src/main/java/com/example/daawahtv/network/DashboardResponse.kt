package com.example.daawahtv.network

// Defines the main response structure for the dashboard
data class DashboardResponse(
    val slider: List<ContentRow>?,
    val banner: List<BannerItem>?
)

// Represents a single row (or slider) of content
data class ContentRow(
    val id: Int,
    val name: String,
    val posts: List<ProgramItem>
)

// Note: 'BannerItem' and 'ProgramItem' are defined in HomeResponse.kt,
// which is okay as long as they are public and imported correctly.