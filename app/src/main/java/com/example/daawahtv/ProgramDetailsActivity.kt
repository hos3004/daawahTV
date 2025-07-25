package com.example.daawahtv

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.daawahtv.network.ApiClient
import com.example.daawahtv.network.Episode
import com.example.daawahtv.network.ProgramDetailsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.daawahtv.network.TvShowDetailsResponse
import com.example.daawahtv.network.ApiClientTv


class ProgramDetailsActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var webView: WebView
    private lateinit var episodesContainer: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_program_details)

        imageView = findViewById(R.id.imageView)
        titleTextView = findViewById(R.id.titleTextView)
        webView = findViewById(R.id.webView)
        episodesContainer = findViewById(R.id.episodesContainer)

        val programId = intent.getLongExtra("programId", -1)
        if (programId == -1L) {
            Toast.makeText(this, "معرف البرنامج غير صالح", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadProgramDetails(programId)
        // "programId" is already a Long, so passing it directly avoids
        // unnecessary conversions that could truncate large values.
        loadEpisodes(programId)
    }


    private fun loadProgramDetails(programId: Long) {
        ApiClientTv.tvShowApiService.getTvShowDetails(programId).enqueue(object : Callback<TvShowDetailsResponse> {
            override fun onResponse(call: Call<TvShowDetailsResponse>, response: Response<TvShowDetailsResponse>) {
                if (response.isSuccessful) {
                    val program = response.body()?.data?.details
                    titleTextView.text = program?.title ?: "بدون عنوان"
                    webView.loadDataWithBaseURL(null, program?.description ?: "", "text/html", "utf-8", null)

                    Glide.with(this@ProgramDetailsActivity)
                        .load(program?.image)
                        .into(imageView)

                } else {
                    Toast.makeText(this@ProgramDetailsActivity, "فشل تحميل البيانات", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TvShowDetailsResponse>, t: Throwable) {
                Toast.makeText(this@ProgramDetailsActivity, "حدث خطأ: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)

    private fun loadEpisodes(programId: Long) {
        ApiClient.apiService.getEpisodesByProgram(programId)
            .enqueue(object : Callback<List<Episode>> {
                override fun onResponse(
                    call: Call<List<Episode>>,
                    response: Response<List<Episode>>
                ) {
                    if (response.isSuccessful) {
                        val episodes = response.body()
                        episodes?.forEach { episode ->
                            val episodeView = layoutInflater.inflate(
                                R.layout.item_episode,
                                episodesContainer,
                                false
                            )

                            val episodeTitle =
                                episodeView.findViewById<TextView>(R.id.episodeTitle)
                            val episodeImage =
                                episodeView.findViewById<ImageView>(R.id.episodeImage)

                            episodeTitle.text = episode.title.rendered
                            Glide.with(this@ProgramDetailsActivity)
                                .load(episode.better_featured_image?.source_url)
                                .into(episodeImage)

                            episodeView.setOnClickListener {
                                val intent = Intent(
                                    this@ProgramDetailsActivity,
                                    PlaybackActivity::class.java
                                )
                                intent.putExtra("url", episode.acf.video_url)
                                startActivity(intent)
                            }

                            episodesContainer.addView(episodeView)
                        }
                    } else {
                        Toast.makeText(
                            this@ProgramDetailsActivity,
                            "فشل تحميل الحلقات",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<Episode>>, t: Throwable) {
                    Toast.makeText(
                        this@ProgramDetailsActivity,
                        "خطأ في الاتصال: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
