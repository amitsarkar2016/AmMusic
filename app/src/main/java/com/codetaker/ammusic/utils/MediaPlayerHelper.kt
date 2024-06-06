package com.codetaker.ammusic.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

class MediaPlayerHelper(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    companion object{
        var currentPosition = 0
        var isPlaying = false
    }

    fun playAudio(audioResourceId: String, completionListener: () -> Unit) {
        releaseMediaPlayer()
        mediaPlayer = MediaPlayer.create(context, Uri.parse(audioResourceId))

        mediaPlayer?.setOnCompletionListener {
            isPlaying = false
            completionListener.invoke()
        }

        mediaPlayer?.start()
        isPlaying = true
    }

    fun pauseAudio() {
        mediaPlayer?.pause()
        currentPosition = mediaPlayer?.currentPosition ?: 0
        isPlaying = false
    }

    fun resumeAudio() {
        mediaPlayer?.seekTo(currentPosition)
        mediaPlayer?.start()
        isPlaying = true
    }

    fun stopAudio() {
        releaseMediaPlayer()
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
    }

    fun isPlaying(): Boolean {
        return isPlaying
    }
}
