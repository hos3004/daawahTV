package com.example.daawahtv.network

/**
 * النموذج الرسمي لرد API عند جلب تفاصيل الحلقة
 */
data class EpisodeDetailsResponse(
    val status: Boolean,
    val message: String,
    val data: EpisodeDetailsData
)

/**
 * يحتوي على بيانات الحلقة مثل:
 * - id
 * - title
 * - رابط الفيديو
 * - منشور مضمّن اختياري
 * - اختيارات إضافية
 * - مصادر الفيديو (إن وجدت)
 */
data class EpisodeDetailsData(
    val id: Int,
    val title: String,
    val url_link: String?,
    val embed_content: String?,
    val episode_choice: String?,
    val sources: List<Any>?
)
