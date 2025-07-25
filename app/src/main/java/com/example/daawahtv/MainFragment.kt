package com.example.daawahtv

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.core.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.daawahtv.network.ApiClient
import com.example.daawahtv.network.HomeResponse
import com.example.daawahtv.network.ProgramItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.net.Uri

class MainFragment : BrowseSupportFragment() {

    private lateinit var mBackgroundManager: BackgroundManager
    private var mDefaultBackground: Drawable? = null
    private lateinit var mMetrics: DisplayMetrics
    private val TAG = "MainFragment"

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prepareBackgroundManager()
        setupUIElements()
        loadContentFromApi()
        setupEventListeners()
    }

    // ✅ --- هذا هو التعديل الأهم ---
    // هذه الدالة يتم استدعاؤها في كل مرة تعود فيها الشاشة للظهور
    override fun onResume() {
        super.onResume()
        updateBackground("https://daawah.tv/app1.jpg")
    }

    private fun loadContentFromApi() {
        ApiClient.apiService.getHomeContent().enqueue(object : Callback<HomeResponse> {
            override fun onResponse(call: Call<HomeResponse>, response: Response<HomeResponse>) {
                if (response.isSuccessful) {
                    val sliders = response.body()?.data?.sliders
                    val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
                    val cardPresenter = CardPresenter()

                    // إضافة العناصر الثابتة
                    val staticItemsAdapter = ArrayObjectAdapter(cardPresenter)
                    staticItemsAdapter.add(programItemToMovie(ProgramItem(-1, "البث المباشر", "https://daawah.tv/live.png", "live_stream"), "القائمة الرئيسية"))
                    staticItemsAdapter.add(programItemToMovie(ProgramItem(-2, "خريطة البرامج", "https://daawah.tv/privacy.png", "privacy_policy"), "القائمة الرئيسية"))
                    staticItemsAdapter.add(programItemToMovie(ProgramItem(-3, "سياسة الخصوصية", "https://daawah.tv/privacy.png", "privacy_policy"), "القائمة الرئيسية"))

                    rowsAdapter.add(ListRow(HeaderItem(0, "القائمة الرئيسية"), staticItemsAdapter))

                    sliders?.forEachIndexed { index, slider ->
                        val rowAdapter = ArrayObjectAdapter(cardPresenter)
                        slider.data.forEach { item ->
                            rowAdapter.add(programItemToMovie(item, slider.title))
                        }
                        rowsAdapter.add(ListRow(HeaderItem(index + 1L, slider.title), rowAdapter))
                    }
                    adapter = rowsAdapter
                }
            }

            override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
                Log.e(TAG, "Network failure", t)
            }
        })
    }

    private fun programItemToMovie(item: ProgramItem, sliderTitle: String): Movie {
        return Movie().apply {
            id = item.id.toLong()
            title = item.title
            description = sliderTitle
            cardImageUrl = item.image ?: ""
            type = item.post_type
        }
    }

    private fun setupEventListeners() {
        setOnSearchClickedListener {
            Toast.makeText(requireContext(), "Search implementation needed", Toast.LENGTH_LONG).show()
        }
        onItemViewClickedListener = ItemViewClickedListener()
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder,
            item: Any,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {
            if (item is Movie) {
                when (item.type) {
                    "live_stream" -> {
                        val intent = Intent(context, PlaybackActivity::class.java).apply {
                            putExtra("url", "http://161.97.100.71/hls/stream.m3u8")
                        }
                        startActivity(intent)
                    }
                    "programs_list" -> {
                        startActivity(Intent(context, ProgramsWebViewActivity::class.java))
                    }
                    "tv_show" -> {
                        val intent = Intent(context, TvShowDetailsActivity::class.java).apply {
                            putExtra("tvShowId", item.id)
                        }
                        startActivity(intent)
                    }
                    "privacy_policy" -> {
                        val intent = Intent(context, PrivacyPolicyActivity::class.java)
                        startActivity(intent)
                    }

                    else -> {
                        Toast.makeText(context, "نوع غير مدعوم: ${item.type}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateBackground(uri: String?) {
        val width = mMetrics.widthPixels
        val height = mMetrics.heightPixels
        Glide.with(requireContext())
            .load(uri)
            .centerCrop()
            .error(mDefaultBackground)
            .into(object : SimpleTarget<Drawable>(width, height) {
                override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>?) {
                    mBackgroundManager.drawable = drawable
                }
            })
    }

    // --- دوال مساعدة أخرى ---
    private fun prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(requireActivity())
        mBackgroundManager.attach(requireActivity().window)
        mDefaultBackground = ContextCompat.getDrawable(requireContext(), R.drawable.default_background)
        mMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(mMetrics)
    }

    private fun setupUIElements() {
        title = getString(R.string.browse_title)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.fastlane_background)
        searchAffordanceColor = ContextCompat.getColor(requireContext(), R.color.search_opaque)
    }
}