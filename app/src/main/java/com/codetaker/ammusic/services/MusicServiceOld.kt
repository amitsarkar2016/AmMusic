package com.codetaker.ammusic.services

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat

class MusicServiceOld : Service() {

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaController: MediaControllerCompat
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_ID = "music_channel"
    }

//    override fun onCreate() {
//        super.onCreate()
//
//        // Initialize MediaSession
//        mediaSession = MediaSessionCompat(this, "MusicService")
//        mediaController = mediaSession.controller
//        mediaSession.isActive = true
//
//        // Set the session's token so that client activities can communicate with it.
////        sessionToken = mediaSession.sessionToken
//
//        // Initialize NotificationManager
//        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Create a notification channel for devices running Android Oreo or higher
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                NOTIFICATION_CHANNEL_ID,
//                "Music",
//                NotificationManager.IMPORTANCE_LOW
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        // Create a media style notification
//        val albumArt = BitmapFactory.decodeResource(resources, R.drawable.album_art)
//        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_music_note)
//            .setContentTitle("Song Title")
//            .setContentText("Artist Name")
//            .setLargeIcon(albumArt)
//            .setPriority(NotificationCompat.PRIORITY_MAX)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .setDeleteIntent(
//                MediaButtonReceiver.buildMediaButtonPendingIntent(
//                    this,
//                    PlaybackStateCompat.ACTION_STOP
//                )
//            )
//            .setStyle(
//                MediaStyle()
//                    .setMediaSession(mediaSession.sessionToken)
//                    .setShowActionsInCompactView(0, 1, 2)
//            )
//
//        // Add action buttons to the media style notification
//        val playIntent = Intent(this, MusicService::class.java)
//        playIntent.action = "ACTION_PLAY"
//        val playPendingIntent = PendingIntent.getService(this, 0, playIntent, 0)
//        notificationBuilder.addAction(
//            R.drawable.ic_play,
//            "Play",
//            playPendingIntent
//        )
//
//        val pauseIntent = Intent(this, MusicService::class.java)
//        pauseIntent.action = "ACTION_PAUSE"
//        val pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, 0)
//        notificationBuilder.addAction(
//            R.drawable.ic_pause,
//            "Pause",
//            pausePendingIntent
//        )
//
//        val nextIntent = Intent(this, MusicService::class.java)
//        nextIntent.action = "ACTION_NEXT"
//        val nextPendingIntent = PendingIntent.getService(this, 0, nextIntent, 0)
//        notificationBuilder.addAction(
//            R.drawable.ic_skip_next,
//            "Next",
//            nextPendingIntent
//        )
//
//        val prevIntent = Intent(this, MusicService::class.java)
//        prevIntent.action = "ACTION_PREVIOUS"
//        val prevPendingIntent = PendingIntent.getService(this, 0, prevIntent, 0)
//        notificationBuilder.addAction(
//            R.drawable.ic_skip_previous,
//            "Previous",
//            prevPendingIntent
//        )
//    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    private val binder = MyBinder()

    inner class MyBinder : Binder() {
        fun getService(): MusicServiceOld {
            return this@MusicServiceOld
        }
    }
}
