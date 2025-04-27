package com.codetaker.ammusic.services

import android.app.PendingIntent
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media2.common.UriMediaItem
import androidx.media2.player.MediaPlayer
import androidx.media2.session.MediaController
import androidx.media2.session.MediaSession
import androidx.media2.session.MediaSessionService
import androidx.media2.session.SessionCommand
import androidx.media2.session.SessionResult
import com.codetaker.ammusic.R
import com.codetaker.ammusic.ui.main.MainActivity
import com.codetaker.ammusic.models.Song
import java.util.concurrent.Executor

class MediaControllerService : MediaSessionService() {

    private lateinit var mediaSession: MediaSession
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        initializeMediaSession()
    }

    private fun initializeMediaSession() {
        mediaPlayer = MediaPlayer(this)
        val executor: Executor = ContextCompat.getMainExecutor(this)

        mediaSession = MediaSession.Builder(this, mediaPlayer)
            .setSessionCallback(executor, MediaSessionCallback())
            .build()

        val sessionActivityIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, sessionActivityIntent, PendingIntent.FLAG_IMMUTABLE)

        val mediaController = MediaController.Builder(this).build()

        mediaSession.updatePlayer(mediaPlayer)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.close()
        mediaPlayer.close()
    }

    private inner class MediaSessionCallback : MediaSession.SessionCallback() {
        override fun onCommandRequest(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            command: SessionCommand
        ): Int {
            return SessionResult.RESULT_SUCCESS
        }

        override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
            updateNotification()
        }
    }

    private fun updateNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Playing")
            .setContentText("Song Title")
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionCompatToken)
                    .setShowActionsInCompactView(0)
            )
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    fun playSong(song: Song) {
        val mediaItem = UriMediaItem.Builder(song.filePath.toUri()).build()
        mediaPlayer.setMediaItem(mediaItem)
        mediaPlayer.prepare().addListener(
            {
                mediaPlayer.play()
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    companion object {
        private const val CHANNEL_ID = "media_playback_channel"
        private const val NOTIFICATION_ID = 1
    }

    inner class LocalBinder : Binder() {
        fun getService(): MediaControllerService = this@MediaControllerService
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return LocalBinder()
    }
}
