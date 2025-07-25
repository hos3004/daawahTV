package com.example.daawahtv

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.daawahtv.adapters.EpisodesAdapter
import com.example.daawahtv.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TvShowDetailsActivity : AppCompatActivity() {

    private val TAG = "DAAWAH_DEBUG"
    private lateinit var detailsRootLayout: ConstraintLayout
    private lateinit var programCoverImageView: ImageView
    private lateinit var programTitleTextView: TextView
    private lateinit var castTextView: TextView
    private lateinit var programDescriptionTextView: TextView
    private lateinit var episodesRecyclerView: RecyclerView
    private var tvShowId: Long = -1L
    private var episodesList: List<EpisodeItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_show_details)

        detailsRootLayout = findViewById(R.id.details_root_layout)
        programCoverImageView = findViewById(R.id.programCoverImageView)
        programTitleTextView = findViewById(R.id.programTitleTextView)
        castTextView = findViewById(R.id.castTextView)
        programDescriptionTextView = findViewById(R.id.programDescriptionTextView)
        episodesRecyclerView = findViewById(R.id.episodesRecyclerView)
        episodesRecyclerView.layoutManager = LinearLayoutManager(this)

        loadAppBackground()

        tvShowId = intent.getLongExtra("tvShowId", -1L)
        if (tvShowId != -1L) {
            loadTvShowDetails(tvShowId)
        }
    }

    private fun loadAppBackground() {
        val backgroundUrl = "https://daawah.tv/d/bgtv1.jpg"
        Glide.with(this)
            .load(backgroundUrl)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    detailsRootLayout.background = resource
                }
                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun loadTvShowDetails(programId: Long) {
        Toast.makeText(this, "جاري تحميل التفاصيل...", Toast.LENGTH_SHORT).show()
        ApiClientTv.tvShowApiService.getTvShowDetails(programId).enqueue(object : Callback<TvShowDetailsResponse> {
            override fun onResponse(call: Call<TvShowDetailsResponse>, response: Response<TvShowDetailsResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val details = response.body()!!.data.details
                    programTitleTextView.text = details.title
                    programDescriptionTextView.text = Html.fromHtml(details.description, Html.FROM_HTML_MODE_LEGACY).toString()
                    details.casts.firstOrNull()?.let { castTextView.text = it.name }
                    if (!details.image.isNullOrEmpty()) {
                        Glide.with(this@TvShowDetailsActivity).load(details.image).into(programCoverImageView)
                    }

                    val firstSeason = details.seasons.data.firstOrNull()
                    if (firstSeason != null) {
                        val seasonId = firstSeason.id
                        loadEpisodesForSeason(programId, seasonId)

                    }
                }
            }

            override fun onFailure(call: Call<TvShowDetailsResponse>, t: Throwable) {
                Log.e(TAG, "Failed to load TV show details", t)
            }
        })
    }

    private fun loadEpisodesForSeason(programId: Long, seasonIndex: Int) {
        val offset = 0
        val limit = 100
        Log.d("Episodes", "Requesting episodes offset=$offset, limit=$limit")
        Toast.makeText(this, "جاري تحميل الحلقات...", Toast.LENGTH_SHORT).show()
        ApiClientTv.tvShowApiService.getSeasonEpisodes(programId, seasonIndex, limit, offset)
            .enqueue(object : Callback<SeasonResponse> {
            override fun onResponse(call: Call<SeasonResponse>, response: Response<SeasonResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val episodes = response.body()!!.data.episodes
                    Log.d("Episodes", "Episodes loaded: ${episodes.size}")

                    if (episodes.isNotEmpty()) {
                        val reversedEpisodes = episodes.reversed()
                        episodesList = reversedEpisodes

                        episodesRecyclerView.adapter = EpisodesAdapter(this@TvShowDetailsActivity, reversedEpisodes) { clickedEpisode, position ->
                            loadVideoUrlForEpisode(clickedEpisode.id, position)
                        }
                        episodesRecyclerView.requestFocus()

                    }
                }
            }

            override fun onFailure(call: Call<SeasonResponse>, t: Throwable) {
                Log.e(TAG, "Failed to load episodes", t)
            }
        })
    }

    private fun loadVideoUrlForEpisode(episodeId: Long, position: Int) {
        Toast.makeText(this, "جاري تحميل الحلقة...", Toast.LENGTH_SHORT).show()
        ApiClientTv.tvShowApiService.getEpisodeDetails(episodeId.toInt())
            .enqueue(object : Callback<EpisodeDetailsResponse> {
                @OptIn(UnstableApi::class)
                override fun onResponse(call: Call<EpisodeDetailsResponse>, response: Response<EpisodeDetailsResponse>) {
                    var videoUrl = response.body()?.data?.url_link

                    if (videoUrl.isNullOrEmpty()) {
                        Toast.makeText(this@TvShowDetailsActivity, "لم يتم العثور على رابط، سيتم تشغيل فيديو اختباري", Toast.LENGTH_LONG).show()
                        // --- ✅ --- استخدام رابط يوتيوب حقيقي وكامل --- ✅ ---
                        videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
                    }

                    // --- ✅ --- شرط أكثر دقة للتحقق من الرابط --- ✅ ---
                    if (videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be")) {
                        Log.d(TAG, "YouTube link detected. Starting YouTubePlayerActivity.")
                        val intent = Intent(this@TvShowDetailsActivity, YouTubePlayerActivity::class.java).apply {
                            putExtra("VIDEO_URL", videoUrl)
                            putExtra("EPISODE_IDS", episodesList.map { it.id }.toLongArray())
                            putExtra("CURRENT_POSITION", position)
                            putExtra("EPISODE_ID", episodeId)
                        }
                        startActivity(intent)
                    } else {
                        Log.d(TAG, "Non-YouTube link detected. Starting PlaybackActivity.")
                        val intent = Intent(this@TvShowDetailsActivity, PlaybackActivity::class.java).apply {
                            putExtra("url", videoUrl)
                            putExtra("EPISODE_IDS", episodesList.map { it.id }.toLongArray())
                            putExtra("CURRENT_POSITION", position)
                            putExtra("EPISODE_ID", episodeId)
                        }
                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<EpisodeDetailsResponse>, t: Throwable) {
                    Toast.makeText(this@TvShowDetailsActivity, "خطأ في الشبكة، سيتم تشغيل فيديو اختباري", Toast.LENGTH_LONG).show()
                    val fallbackUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
                    val intent = Intent(this@TvShowDetailsActivity, YouTubePlayerActivity::class.java).apply {
                        putExtra("VIDEO_URL", fallbackUrl)
                        putExtra("EPISODE_ID", episodeId)
                    }
                    startActivity(intent)
                }
            })
    }
}