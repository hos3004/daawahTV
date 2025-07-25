package com.example.daawahtv

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.daawahtv.network.Episode

class EpisodeAdapter(
    private val context: Context,
    private val episodes: List<Episode>
) : RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode, parent, false)
        return EpisodeViewHolder(view)
    }


    @OptIn(UnstableApi::class)
    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = episodes[position]

        holder.title.text = episode.title.rendered

        // Show duration if available
        if (!episode.acf.duration.isNullOrBlank()) {
            holder.duration.text = "المدة: ${episode.acf.duration}"
            holder.duration.visibility = View.VISIBLE
        } else {
            holder.duration.visibility = View.GONE
        }

        Glide.with(context)
            .load(episode.better_featured_image?.source_url)
            .placeholder(R.drawable.default_background)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PlaybackActivity::class.java)
            intent.putExtra("url", episode.acf.video_url)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = episodes.size

    inner class EpisodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.episodeImage)
        val title: TextView = view.findViewById(R.id.episodeTitle)
        val duration: TextView = view.findViewById(R.id.episodeDuration)
    }
}
