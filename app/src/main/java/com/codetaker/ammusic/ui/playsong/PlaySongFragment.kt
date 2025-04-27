package com.codetaker.ammusic.ui.playsong

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.codetaker.ammusic.R
import com.codetaker.ammusic.core.RepeatMode
import com.codetaker.ammusic.databinding.FragmentPlaySongBinding
import com.codetaker.ammusic.models.Song
import com.codetaker.ammusic.services.MusicService
import com.codetaker.ammusic.utils.GeneralFunctions.formatTime

class PlaySongFragment : Fragment(R.layout.fragment_play_song) {
    private var _binding: FragmentPlaySongBinding? = null
    private val binding get() = _binding!!
    private var musicService: MusicService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true
            musicService?.setOnUpdateProgressListener { progress ->
                _binding?.seekBar?.progress = progress
                _binding?.currentDuration?.text = formatTime(progress.toLong())
            }
            updateUI()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            musicService = null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlaySongBinding.bind(view)

        Intent(requireContext(), MusicService::class.java).also { intent ->
            requireActivity().startService(intent) // Start the service
            requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        clickListeners()

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && isBound) {
                    musicService?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }
        })
    }

    private fun clickListeners() {
        binding.playPauseBtn.setOnClickListener {
            if (isBound) {
                if (musicService?.playPause() == true) {
                    binding.playPauseBtn.setImageResource(R.drawable.ic_pause_button)
                } else {
                    binding.playPauseBtn.setImageResource(R.drawable.ic_play_button)
                }
            }
        }
        binding.nextBtn.setOnClickListener {
            if (isBound) {
                musicService?.nextSong { song ->
                    showMusicPlayer(song)
                }
            }
        }
        binding.prevBtn.setOnClickListener {
            if (isBound) {
                musicService?.previousSong { song ->
                    showMusicPlayer(song)
                }
            }
        }

        binding.favoriteBtn.setOnClickListener {
            if (isBound) {
                musicService?.toggleFavorite {
                    if (it) {
                        binding.favoriteBtn.setImageResource(R.drawable.ic_favorite)
                    } else {
                        binding.favoriteBtn.setImageResource(R.drawable.ic_favorite_border)
                    }
                    // update database as well musicService?.updateFavorite(song)
                    
                }
            }
        }

        binding.shuffleBtn.setOnClickListener {
            if (isBound) {
                musicService?.shuffle()
            }
        }
        binding.repeatBtn.setOnClickListener {
            if (isBound) {
                musicService?.repeat {
                    when (it) {
                        RepeatMode.NONE -> {
                            binding.repeatBtn.setImageResource(R.drawable.ic_repeat)
                        }
                        RepeatMode.ALL -> {
                            binding.repeatBtn.setImageResource(R.drawable.ic_repeat_all)
                        }
                        RepeatMode.ONE -> {
                            binding.repeatBtn.setImageResource(R.drawable.ic_repeat_one)
                        }
                    }
                }
            }
        }

        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun showMusicPlayer(song: Song) {
        binding.songName.text = song.title
        binding.songArtist.text = song.artist
        binding.finalDuration.text = formatTime(song.duration)
        binding.seekBar.max = song.duration.toInt()
        binding.playPauseBtn.setImageResource(R.drawable.ic_pause_button)
        binding.seekBar.progress = 0
        binding.albumArt.setImageURI(Uri.parse(song.albumArtPath))
        Log.d("PlaySongFragment", "showMusicPlayer: ${song.albumArtPath}")
    }

    private fun updateUI() {
        if (musicService?.isMusicPlaying() == true) {
            val currentSong = musicService?.currentSong()
            currentSong?.let { showMusicPlayer(it) }
            binding.playPauseBtn.setImageResource(R.drawable.ic_pause_button)
        } else {
            binding.playPauseBtn.setImageResource(R.drawable.ic_play_button)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isBound) {
            updateUI()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (isBound) {
            requireActivity().unbindService(serviceConnection)
            isBound = false
        }
    }
}
