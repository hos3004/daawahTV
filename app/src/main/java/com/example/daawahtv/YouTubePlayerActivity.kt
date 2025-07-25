package com.example.daawahtv

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.example.daawahtv.PlaybackPositionManager
import com.example.daawahtv.network.ApiClientTv
import com.example.daawahtv.network.EpisodeDetailsResponse
import java.util.regex.Pattern

class YouTubePlayerActivity : AppCompatActivity() {

    private val TAG = "YouTubePlayerActivity"
    private var episodeIds: LongArray? = null
    private var currentPosition: Int = 0
    private var currentEpisodeId: Long = -1
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_player)

        val videoUrl = intent.getStringExtra("VIDEO_URL")
        episodeIds = intent.getLongArrayExtra("EPISODE_IDS")
        currentPosition = intent.getIntExtra("CURRENT_POSITION", 0)
        currentEpisodeId = intent.getLongExtra("EPISODE_ID", -1L)
        if (currentEpisodeId == -1L && episodeIds != null && currentPosition < episodeIds!!.size) {
            currentEpisodeId = episodeIds!![currentPosition]
        }
        if (videoUrl.isNullOrEmpty()) {
            Log.e(TAG, "VIDEO_URL is missing!")
            finish()
            return
        }

        val videoId = extractYouTubeId(videoUrl)
        if (videoId.isNullOrEmpty()) {
            Log.e(TAG, "Could not extract YouTube video ID from URL: $videoUrl")
            // يمكنك هنا عرض رسالة خطأ للمستخدم قبل إنهاء الشاشة
            finish()
            return
        }

        Log.d(TAG, "Extracted video ID: $videoId")

        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        // السماح بتشغيل الفيديو تلقائياً
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()
        webView.addJavascriptInterface(object {
            @JavascriptInterface
            fun onVideoEnded() {
                runOnUiThread { playNextEpisode() }
            }
        }, "Android")

        val resumeMs = PlaybackPositionManager.getPosition(this, currentEpisodeId)
        val startSeconds = resumeMs / 1000

        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body, html { margin: 0; padding: 0; height: 100%; overflow: hidden; background-color: #000; }
                    #player { position: absolute; top: 0; left: 0; width: 100%; height: 100%; }
                </style>
                <script src="https://www.youtube.com/iframe_api"></script>
                <script>
                    var player;
                    function onYouTubeIframeAPIReady() {
                        player = new YT.Player('player', {
                            height: '100%',
                            width: '100%',
                            videoId: '$videoId',
                            playerVars: { 'autoplay': 1, 'start': $startSeconds },
                            events: {
                                'onStateChange': function(event) {
                                    if(event.data == YT.PlayerState.ENDED) {
                                        Android.onVideoEnded();
                                    }
                                }
                            }
                        });
                    }
                </script>
            </head>
            <body>
                <div id="player"></div>
            </body>
            </html>
        """.trimIndent()

        // استخدام 'null' كـ baseURL يسمح للـ WebView بتحميل المحتوى بشكل صحيح
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
    }

    override fun onPause() {
        super.onPause()
        if (::webView.isInitialized) {
            webView.evaluateJavascript("(player && player.getCurrentTime()) || 0") { result ->
                val seconds = result.replace("\"", "").toDoubleOrNull() ?: 0.0
                PlaybackPositionManager.savePosition(this, currentEpisodeId, (seconds * 1000).toLong())
            }
        }
    }

    private fun playNextEpisode() {
        val ids = episodeIds ?: return
        if (currentPosition + 1 >= ids.size) {
            finish()
            return
        }
        currentPosition += 1
        val nextId = ids[currentPosition]
        PlaybackPositionManager.savePosition(this, currentEpisodeId, 0)
        currentEpisodeId = nextId
        Toast.makeText(this, "تشغيل الحلقة التالية...", Toast.LENGTH_SHORT).show()
        ApiClientTv.tvShowApiService.getEpisodeDetails(nextId.toInt())
            .enqueue(object : retrofit2.Callback<EpisodeDetailsResponse> {
                override fun onResponse(
                    call: retrofit2.Call<EpisodeDetailsResponse>,
                    response: retrofit2.Response<EpisodeDetailsResponse>
                ) {
                    var url = response.body()?.data?.url_link
                    if (url.isNullOrEmpty()) {
                        url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
                    }
                    if (url.contains("youtube.com") || url.contains("youtu.be")) {
                        val intent = intent
                        intent.putExtra("VIDEO_URL", url)
                        intent.putExtra("CURRENT_POSITION", currentPosition)
                        intent.putExtra("EPISODE_IDS", ids)
                        intent.putExtra("EPISODE_ID", nextId)
                        finish()
                        startActivity(intent)
                    } else {
                        val pIntent = android.content.Intent(this@YouTubePlayerActivity, PlaybackActivity::class.java).apply {
                            putExtra("url", url)
                            putExtra("EPISODE_IDS", ids)
                            putExtra("CURRENT_POSITION", currentPosition)
                            putExtra("EPISODE_ID", nextId)
                        }
                        startActivity(pIntent)
                        finish()
                    }
                }

                override fun onFailure(call: retrofit2.Call<EpisodeDetailsResponse>, t: Throwable) {
                    finish()
                }
            })
    }

    private fun extractYouTubeId(ytUrl: String): String? {
        var videoId: String? = null
        val regex = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2Fvideos%2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(ytUrl)
        if (matcher.find()) {
            videoId = matcher.group()
        }
        return videoId
    }
}