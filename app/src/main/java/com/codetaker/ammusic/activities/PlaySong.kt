package com.codetaker.ammusic.activities

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.codetaker.ammusic.R
import com.codetaker.ammusic.databinding.ActivityPlaySongBinding
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit

class PlaySong : AppCompatActivity() {
    private lateinit var binding: ActivityPlaySongBinding
    var mediaPlayer: MediaPlayer? = null
    var position = 0
    private var songs: ArrayList<File>? = null
    private var stopThread = false
    private var textContent: String? = null
    private var updateSeek: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo((mediaPlayer?.duration ?: 0) * progress / 100)
                }
            }

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

    private fun seekBarUpdate() {
        updateSeek = Thread {
            while (!stopThread) {
                try {
                    Thread.sleep(200)
                    runOnUiThread {
                        val currentPosition = mediaPlayer?.currentPosition ?: 0
                        val duration = mediaPlayer?.duration ?: 1
                        binding.seekBar.progress =
                            if (duration > 0) currentPosition * 100 / duration else 0
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

    private fun switchSong() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, Uri.parse(songs!![position].toString()))
        textContent = songs?.get(position)?.name
        binding.songName.text = textContent
        binding.play.setImageResource(R.drawable.pause)
        mediaPlayer?.setOnPreparedListener {
            mediaPlayer?.start()
        }
//        updateNotification(true)
    }


    private fun formatTime(time: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time) % TimeUnit.MINUTES.toSeconds(1)
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopThread = true
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
    }
}