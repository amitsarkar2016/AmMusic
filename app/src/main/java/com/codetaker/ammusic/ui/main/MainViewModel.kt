package com.codetaker.ammusic.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codetaker.ammusic.models.Song
import com.codetaker.ammusic.data.repositories.SongsRepository

class MainViewModel(
    private val repository: SongsRepository,
) : ViewModel() {


    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage


    val songsList: LiveData<List<Song>> = repository.songsList

    init {
        loadSongs()
    }

    private fun loadSongs() {
        try {
            repository.loadSongs()
        } catch (e: Exception) {
            _errorMessage.postValue("Error loading songs: ${e.message}")
        }
    }

    fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }

    fun setCurrentPosition(position: Int) {
        repository.setCurrentPosition(position)
    }
}
