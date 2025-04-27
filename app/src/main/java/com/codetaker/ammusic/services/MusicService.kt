package com.codetaker.ammusic.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.codetaker.ammusic.R
import com.codetaker.ammusic.core.RepeatMode
import com.codetaker.ammusic.models.Song

class MusicService : Service() {
    private val binder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null
    private val CHANNEL_ID = "MusicServiceChannel"
    private val handler = Handler(Looper.getMainLooper())
    private var onUpdateProgress: ((Int) -> Unit)? = null
    private var songQueue: List<Song> = listOf()
    private var currentIndex: Int = 0
    private var isShuffle = false
    private var isRepeat = false
    private var repeatMode: RepeatMode = RepeatMode.NONE


    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        return START_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return false
    }

    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Music Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Playing Music")
            .setContentText("Your music is playing")
            .setSmallIcon(R.drawable.logo)
            .build()
    }

    fun setQueue(queue: List<Song>, startIndex: Int) {
        songQueue = queue
        currentIndex = startIndex
        playSong(queue[startIndex])
    }

    fun playSong(song: Song) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(song.filePath)
            prepare()
            start()
            setOnCompletionListener {
                if (isRepeat && repeatMode == RepeatMode.ONE) {
                    playSong(songQueue[currentIndex])
                } else {
                    nextSong()
                }
            }
        }
        startUpdatingSeekBar()
    }

    fun playPause(): Boolean {
        mediaPlayer?.let {
            return if (it.isPlaying) {
                it.pause()
                false
            } else {
                it.start()
                startUpdatingSeekBar()
                true
            }
        }
        return false
    }

    fun nextSong(onNext: ((song: Song) -> Unit)? = null) {
        if (currentIndex < songQueue.size - 1) {
            currentIndex++
            playSong(songQueue[currentIndex])
        } else {
            currentIndex = 0
            playSong(songQueue[currentIndex])
        }
        onNext?.invoke(songQueue[currentIndex])
    }

    fun previousSong(onPrevious: ((song: Song) -> Unit)? = null) {
        if (currentIndex > 0) {
            currentIndex--
            playSong(songQueue[currentIndex])
        } else {
            currentIndex = songQueue.size - 1
            playSong(songQueue[currentIndex])
        }
        onPrevious?.invoke(songQueue[currentIndex])
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun setOnUpdateProgressListener(listener: (Int) -> Unit) {
        onUpdateProgress = listener
    }

    private fun startUpdatingSeekBar() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    onUpdateProgress?.invoke(it.currentPosition)
                }
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    fun isMusicPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun currentSong(): Song? {
        return if (songQueue.isNotEmpty()) songQueue[currentIndex] else null
    }

    fun shuffle() {
        val shuffledQueue = songQueue.shuffled()
        currentIndex = 0
        playSong(shuffledQueue[currentIndex])
    }

    fun repeat(onRepeat: (repeatMode: RepeatMode) -> Unit) {
        when (repeatMode) {
            RepeatMode.NONE -> {
                isRepeat = true
                repeatMode = RepeatMode.ONE
                onRepeat(RepeatMode.ONE)
            }

            RepeatMode.ONE -> {
                isRepeat = true
                repeatMode = RepeatMode.ALL
                onRepeat(RepeatMode.ALL)
            }

            RepeatMode.ALL -> {
                isRepeat = false
                repeatMode = RepeatMode.NONE
                onRepeat(RepeatMode.NONE)
            }
        }
    }

    fun toggleFavorite(onFavorite: (isFavorite: Boolean) -> Unit) {
        songQueue[currentIndex].isFavorite = !songQueue[currentIndex].isFavorite
        onFavorite(songQueue[currentIndex].isFavorite)
    }
}
