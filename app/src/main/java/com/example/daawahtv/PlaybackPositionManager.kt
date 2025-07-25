package com.example.daawahtv

import android.content.Context
import android.content.SharedPreferences

object PlaybackPositionManager {
    private const val PREFS_NAME = "playback_positions"

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun savePosition(context: Context, episodeId: Long, position: Long) {
        prefs(context).edit().putLong(episodeKey(episodeId), position).apply()
    }

    fun getPosition(context: Context, episodeId: Long): Long {
        return prefs(context).getLong(episodeKey(episodeId), 0L)
    }

    private fun episodeKey(episodeId: Long) = "episode_$episodeId"
}
