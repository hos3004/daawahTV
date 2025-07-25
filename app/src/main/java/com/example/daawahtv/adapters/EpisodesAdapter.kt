package com.example.daawahtv.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.daawahtv.R
import com.example.daawahtv.network.EpisodeItem // ✅ استخدام نموذج EpisodeItem الصحيح

class EpisodesAdapter(
    private val context: Context,
    private val episodes: List<EpisodeItem>, // ✅ استخدام قائمة من نوع EpisodeItem
    private val onEpisodeClick: (EpisodeItem, Int) -> Unit
) : RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        // افترض أنك تستخدم تصميم item_episode_card.xml
        val view = LayoutInflater.from(context).inflate(R.layout.item_episode_card, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = episodes[position]
        holder.bind(episode)
        holder.itemView.setOnClickListener {
            onEpisodeClick(episode, position)
        }
    }

    override fun getItemCount(): Int = episodes.size

    inner class EpisodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val episodeTitleTextView: TextView = itemView.findViewById(R.id.episodeTitleTextView)

        fun bind(episode: EpisodeItem) {
            // ✅ الوصول للعنوان مباشرة لأنه String
            episodeTitleTextView.text = episode.title

            val imageUrl = episode.image
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.default_background)
            }
        }
    }
}