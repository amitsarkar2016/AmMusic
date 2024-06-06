package com.codetaker.ammusic.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.codetaker.ammusic.R
import com.codetaker.ammusic.adapters.SongListAdapter
import com.codetaker.ammusic.callback.SongListCallBack
import com.codetaker.ammusic.databinding.FragmentHomeBinding
import com.codetaker.ammusic.db.AppDatabase
import com.codetaker.ammusic.models.Song
import com.codetaker.ammusic.repositories.SongsRepository
import com.codetaker.ammusic.services.MusicService
import com.codetaker.ammusic.viewmodels.HomeViewModel
import com.codetaker.ammusic.viewmodels.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

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
    }

    private fun clickListeners() {
        binding.playPauseBtn.setOnClickListener {
            if (isBound) {
                musicService?.playPause()
            }
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
            musicService?.playSong(song)
            showMusicPlayer(song)
        }
    }

    private fun showMusicPlayer(song: Song) {
        binding.songName.text = song.title
        binding.songArtist.text = song.artist
        binding.musicPlayLL.isVisible = true
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
