package com.example.daawahtv.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvShowApiService {

    // دالة جلب تفاصيل البرنامج (للحصول على المواسم)
    @GET("tv-shows/{id}")
    fun getTvShowDetails(
        @Path("id") id: Long
    ): Call<TvShowDetailsResponse>

    // دالة جلب حلقات الموسم
    @GET("tv-shows/{tv_show_id}/seasons/{season_id}")
    fun getSeasonEpisodes(
        @Path("tv_show_id") tvShowId: Long,
        @Path("season_id") seasonId: Int,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): Call<SeasonResponse>


    // ✅ --- الدالة الجديدة والمهمة ---
    // دالة جلب تفاصيل الحلقة الواحدة للحصول على رابط الفيديو
    @GET("tv-show/season/episodes/{episode_id}")
    fun getEpisodeDetails(
        @Path("episode_id") episodeId: Int
    ): Call<EpisodeDetailsResponse>
}