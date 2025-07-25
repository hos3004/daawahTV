package com.example.daawahtv.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("dashboard/home")
    fun getHomeContent(): Call<HomeResponse>

    // ✅ المسار الصحيح لجلب تفاصيل البرنامج
    @GET("tv-shows/{id}")
    fun getTvShowDetails(@Path("id") id: Long): Call<TvShowDetailsResponse>

    // ✅ المسار الصحيح لجلب الحلقات
    @GET("tv-show/season/episodes")
    fun getEpisodesByProgram(
        @Query("program") programId: Long
    ): Call<List<Episode>>

    // ❌ تم حذف الدوال المكررة وغير المستخدمة
}