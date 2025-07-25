package com.example.daawahtv.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EpisodesApiService {
    // ✅ تم تعديل الدالة لتستخدم List<Episode> مباشرة
    // وتصحيح المسار ليتوافق مع ApiService الرئيسي
    @GET("tv-show/season/episodes")
    fun getEpisodesByProgram(
        @Query("program") programId: Int
    ): Call<List<Episode>> // ✅ تم تصحيح نوع الإرجاع

    // تم حذف النماذج المكررة من هنا لأنها معرفة في ملفات أخرى
}