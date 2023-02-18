package com.codetaker.ammusic.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.codetaker.ammusic.R
import com.codetaker.ammusic.databinding.ActivityPlaySongBinding
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class PlaySong : AppCompatActivity() {
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        binding = ActivityPlaySongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        songs = intent.getParcelableArrayListExtra<Parcelable>("songList") as ArrayList<File>?
        position = intent.getIntExtra("position", 0)
        binding.songName.isSelected = true
        textContent = songs?.get(position)?.name
        binding.songName.text = textContent
        binding.seekBar.max = 100
        mediaPlayer = MediaPlayer.create(this, Uri.parse(songs!![position].toString()))

        mediaPlayer?.setOnPreparedListener {
            mediaPlayer?.start()
        }
        mediaPlayer?.setOnCompletionListener {
            position++
            if (position >= songs!!.size) {
                position = 0
            }
            switchSong()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) { if (fromUser) { mediaPlayer?.seekTo((mediaPlayer?.duration ?: 0) * progress / 100) } }
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        binding.play.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                binding.play.setImageResource(R.drawable.play)
                mediaPlayer?.pause()
            } else {
                binding.play.setImageResource(R.drawable.pause)
                mediaPlayer?.start()
            }
        }
        binding.previous.setOnClickListener {
            position--
            if (position < 0) {
                position = songs!!.size - 1
            }
            switchSong()
        }
        binding.next.setOnClickListener {
            position++
            if (position >= songs!!.size) {
                position = 0
            }
            switchSong()
        }
        seekBarUpdate()
    }
    private fun playNextSong() {
        position++
        if (position >= songs!!.size) {
            position = 0
        }
        switchSong()
    }
    private fun seekBarUpdate() {
        updateSeek = Thread {
            while (!stopThread) {
                try {
                    Thread.sleep(200)
                    runOnUiThread {
                        val currentPosition = mediaPlayer?.currentPosition ?: 0
                        val duration = mediaPlayer?.duration ?: 1
                        binding.seekBar.progress = if (duration > 0) currentPosition * 100 / duration else 0
                        binding.firstDuration.text = formatTime(currentPosition.toLong())
                        binding.secondDuration.text = formatTime(duration.toLong())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        updateSeek?.start()
    }

    fun switchSong() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, Uri.parse(songs!![position].toString()))
        textContent = songs?.get(position)?.name
        binding.songName.text = textContent
        binding.play.setImageResource(R.drawable.pause)
        mediaPlayer?.setOnPreparedListener {
            mediaPlayer?.start()
        }
        createNotification(songs!![position])
//        updateNotification(true)
    }


    private fun formatTime(time: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time) % TimeUnit.MINUTES.toSeconds(1)
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaController: MediaControllerCompat
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val prevPendingIntent: PendingIntent by lazy { createPendingIntent(ACTION_PREVIOUS) }
    private val pausePendingIntent: PendingIntent by lazy { createPendingIntent(ACTION_PAUSE) }
    private val nextPendingIntent: PendingIntent by lazy { createPendingIntent(ACTION_NEXT) }
    private val playPendingIntent: PendingIntent by lazy { createPendingIntent(ACTION_PLAY) }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_ID = "music_channel"
        const val EXTRA_ACTION = "com.codetaker.ammusic.extra.ACTION"
        const val _ACTION_PLAY = "com.codetaker.ammusic.action.ACTION_PLAY"
        const val _ACTION_PAUSE = "com.codetaker.ammusic.action.ACTION_PAUSE"
        const val _ACTION_NEXT = "com.codetaker.ammusic.action.ACTION_NEXT"
        const val _ACTION_PREVIOUS = "com.codetaker.ammusic.action.ACTION_PREVIOUS"
        const val ACTION_PREVIOUS = "0"
        const val ACTION_PAUSE = "1"
        const val ACTION_NEXT = "2"
        const val ACTION_PLAY = "3"
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_ACTION, action)
        }
        return PendingIntent.getBroadcast(this, 0, intent, if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
    }

    private fun createNotification(mySongs: File) {
        mediaSession = MediaSessionCompat(this, "MusicService")
        mediaController = MediaControllerCompat(this, mediaSession.sessionToken)
        mediaSession.isActive = true
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Music",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Create a media style notification
        val albumArt = BitmapFactory.decodeResource(resources, R.drawable.logo)
        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(mySongs.name)
            .setContentText(mySongs.name)
            .setLargeIcon(albumArt)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .addAction(R.drawable.previous, "Previous", prevPendingIntent)
            .addAction(R.drawable.pause, "Pause", pausePendingIntent)
            .addAction(R.drawable.next, "Next", nextPendingIntent)

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun updateNotification(isPlaying: Boolean) {
        val pausePlayIcon: Int = if (isPlaying) {
            R.drawable.pause
        } else {
            R.drawable.play
        }
        val pausePlayTitle: String = if (isPlaying) {
            "Pause"
        } else {
            "Play"
        }

        // update the notification builder with the new actions
        notificationBuilder
//            .mActions.clear()
            .addAction(R.drawable.previous, "Previous", prevPendingIntent)
            .addAction(pausePlayIcon, pausePlayTitle, pausePendingIntent)
            .addAction(R.drawable.next, "Next", nextPendingIntent)

        // update the notification
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }


    val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_PLAY -> {
                    // Update media player to play song
                    mediaPlayer?.start()
                    // Update notification to show pause button
//                    notificationBuilder.mActions.removeAt(1)
                    notificationBuilder.addAction(R.drawable.pause, "Pause", pausePendingIntent)
                    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                }
                ACTION_PAUSE -> {
                    // Update media player to pause song
                    mediaPlayer?.pause()
                    // Update notification to show play button
//                    notificationBuilder.mActions.removeAt(1)
                    notificationBuilder.addAction(R.drawable.play, "Play", playPendingIntent)
                    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                }
                ACTION_NEXT -> {
                    // Update media player to play next song
                    position++
                    if (position >= songs!!.size) {
                        position = 0
                    }
                    switchSong()
                    // Update notification to show new song info and pause button
                    notificationBuilder.setContentTitle(songs?.get(position)?.name)
                    notificationBuilder.setContentText(songs?.get(position)?.name)
//                    notificationBuilder.mActions.removeAt(1)
                    notificationBuilder.addAction(R.drawable.pause, "Pause", pausePendingIntent)
                    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                }
                ACTION_PREVIOUS -> {
                    // Update media player to play next song
                    position--
                    if (position >= songs!!.size) {
                        position = 0
                    }
                    switchSong()
                    // Update notification to show new song info and pause button
                    notificationBuilder.setContentTitle(songs?.get(position)?.name)
                    notificationBuilder.setContentText(songs?.get(position)?.name)
//                    notificationBuilder.mActions.removeAt(1)
                    notificationBuilder.addAction(R.drawable.pause, "Pause", pausePendingIntent)
                    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        stopThread = true
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
    }
    var mediaPlayer: MediaPlayer? = null
    var position = 0
    var songs: ArrayList<File>? = null
    var stopThread = false
    var textContent: String? = null
    var updateSeek: Thread? = null
    lateinit var binding: ActivityPlaySongBinding
}