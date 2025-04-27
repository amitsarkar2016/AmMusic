package com.codetaker.ammusic.utils

import java.util.Locale
import java.util.concurrent.TimeUnit

object GeneralFunctions {
    fun formatTime(time: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time) % TimeUnit.MINUTES.toSeconds(1)
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}