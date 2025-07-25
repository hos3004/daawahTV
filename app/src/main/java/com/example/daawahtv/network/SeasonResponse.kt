package com.example.daawahtv.network

// النموذج الرئيسي الذي يمثل كامل الاستجابة
data class SeasonResponse(
    val status: Boolean,
    val message: String,
    val data: SeasonData
)

// النموذج الذي يحتوي على تفاصيل الموسم وقائمة الحلقات
data class SeasonData(
    val name: String,
    val episodes: List<EpisodeItem>
)

// نموذج الحلقة كما هو معرف داخل استجابة الموسم
data class EpisodeItem(
    val id: Long, // ✅ تم التعديل هنا إلى Long
    val title: String,
    val image: String?,
    val post_type: String?,
    val run_time: String?,
    val release_date: String?
)