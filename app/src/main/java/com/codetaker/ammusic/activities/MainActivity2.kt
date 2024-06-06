package com.codetaker.ammusic.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.codetaker.ammusic.utils.MediaPlayerHelper
import com.codetaker.ammusic.databinding.ActivityPlaySongBinding
import com.codetaker.ammusic.viewmodels.MainViewModel

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityPlaySongBinding
    private lateinit var mediaPlayerHelper: MediaPlayerHelper

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(javaClass.simpleName, System.currentTimeMillis().toString())
        binding = ActivityPlaySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPlayerHelper = MediaPlayerHelper(this)

        viewModel.songsList.observe(this) { songs ->
            if (songs.isNotEmpty()) {
                playSong(viewModel.getCurrentPosition())

                binding.next.setOnClickListener {
                    var currentPosition = viewModel.getCurrentPosition()
                    currentPosition++
                    if (currentPosition >= songs.size) {
                        currentPosition = 0
                    }
                    viewModel.setCurrentPosition(currentPosition)
                    playSong(currentPosition)
                }
            }
        }
    }

    private fun playSong(position: Int) {
        val songs = viewModel.songsList.value ?: return
        val song = songs[position]
        mediaPlayerHelper.run {
            playAudio(song.filePath) {
                var currentPosition = viewModel.getCurrentPosition()
                currentPosition++
                if (currentPosition >= songs.size) {
                    currentPosition = 0
                }
                viewModel.setCurrentPosition(currentPosition)
                playSong(currentPosition)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerHelper.releaseMediaPlayer()
    }
}
