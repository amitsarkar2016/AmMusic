package com.codetaker.ammusic

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.codetaker.ammusic.databinding.ActivityPlaySongBinding
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class PlaySong : AppCompatActivity() {
    var mediaPlayer: MediaPlayer? = null
    var position = 0
    var songs: ArrayList<File>? = null
    var stopThread = false
    var textContent: String? = null
    var updateSeek: Thread? = null
    lateinit var binding: ActivityPlaySongBinding

    override fun onDestroy() {
        super.onDestroy()
        stopThread = true
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        binding = ActivityPlaySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the list of songs and current position from the intent
        songs = intent.getParcelableArrayListExtra<Parcelable>("songList") as ArrayList<File>?
        position = intent.getIntExtra("position", 0)

        // Set up the UI elements
        binding.songName.isSelected = true
        textContent = songs?.get(position)?.name
        binding.songName.text = textContent
        binding.seekBar.max = 100

        // Set up the media player with the first song and start playing it
        mediaPlayer = MediaPlayer.create(this, Uri.parse(songs!![position].toString()))
        mediaPlayer?.start()

        // Set up the seek bar listener to update the progress and switch to the next song when finished
        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo((mediaPlayer?.duration ?: 0) * progress / 100)
                }
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        // Set up the play/pause button
        binding.play.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                binding.play.setImageResource(R.drawable.play)
                mediaPlayer?.pause()
            } else {
                binding.play.setImageResource(R.drawable.pause)
                mediaPlayer?.start()
            }
        }

        // Set up the previous and next buttons
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

        // Set up the thread to update the seek bar and time text views
        updateSeek = Thread {
            while (!stopThread) {
                try {
                    runOnUiThread {
                        val currentPosition = mediaPlayer?.currentPosition ?: 0
                        val duration = mediaPlayer?.duration ?: 1
                        binding.seekBar.progress = currentPosition * 100 / duration
                        binding.firstDuration.text = formatTime(currentPosition.toLong())
                        binding.secondDuration.text = formatTime(duration.toLong())
                    }
                    Thread.sleep(200)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        updateSeek?.start()

        mediaPlayer?.setOnCompletionListener {
            position++
            if (position >= songs!!.size) {
                position = 0
            }
            switchSong()
        }
    }
    private fun switchSong() {
        // Release the current media player and create a new one with the next song
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, Uri.parse(songs!![position].toString()))

        // Set up the UI elements with the new song information
        textContent = songs?.get(position)?.name
        binding.songName.text = textContent
        binding.play.setImageResource(R.drawable.pause)

        // Start playing the new song
        mediaPlayer?.start()
    }

    private fun formatTime(time: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time) % TimeUnit.MINUTES.toSeconds(1)
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}