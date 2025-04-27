package com.codetaker.ammusic.ui.home

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.codetaker.ammusic.R
import com.codetaker.ammusic.databinding.FragmentHomeBinding
import com.codetaker.ammusic.data.local.db.AppDatabase
import com.codetaker.ammusic.extensions.replaceFragmentIfNeeded
import com.codetaker.ammusic.models.Song
import com.codetaker.ammusic.data.repositories.SongsRepository
import com.codetaker.ammusic.services.MusicService
import com.codetaker.ammusic.ui.playsong.PlaySongFragment
import com.codetaker.ammusic.utils.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home), SongListCallBack {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var songListAdapter: SongListAdapter
    private var musicService: MusicService? = null
    private var isBound = false

    private val viewModel: HomeViewModel by viewModels {
        val appDatabase = AppDatabase.getDatabase(requireActivity().application)
        val repository = SongsRepository(requireActivity().application, appDatabase)
        ViewModelFactory(HomeViewModel::class.java) { HomeViewModel(repository) }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true
            musicService?.setOnUpdateProgressListener { progress ->
                _binding?.seekBar?.progress = progress
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            musicService = null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        songListAdapter = SongListAdapter(this)
        binding.songsRV.adapter = songListAdapter

        viewModelHandle()

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
        binding.musicPlayLL.setOnClickListener {
            replaceFragmentIfNeeded(PlaySongFragment(), R.id.mainContainer)
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getSongs()
            binding.swipeRefresh.isRefreshing = false
        }

    }

    private fun viewModelHandle() {
        viewModel.songsList.observe(viewLifecycleOwner) { songs ->
            songListAdapter.submitList(songs)
        }
    }

    override fun onClick(song: Song) {
        viewModel.setCurrentPosition(songListAdapter.currentList.indexOf(song))
        if (isBound) {
            musicService?.setQueue(
                songListAdapter.currentList, songListAdapter.currentList.indexOf(song)
            )
            showMusicPlayer(song)
        }
    }

    private fun showMusicPlayer(song: Song) {
        binding.songName.text = song.title
        binding.songArtist.text = song.artist
        binding.musicPlayLL.isVisible = true
        binding.playPauseBtn.setImageResource(R.drawable.ic_pause_button)
    }

    private fun updateUI() {
        if (musicService?.isMusicPlaying() == true) {
            val currentSong = musicService?.currentSong()
            currentSong?.let { showMusicPlayer(it) }
            binding.playPauseBtn.setImageResource(R.drawable.ic_pause_button)
        } else {
            binding.playPauseBtn.setImageResource(R.drawable.ic_play_button)
        }
        binding.musicPlayLL.isVisible = musicService?.isMusicPlaying() == true
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(100)
            if (isBound) {
                updateUI()
            }
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
