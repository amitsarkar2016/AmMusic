package com.codetaker.ammusic.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codetaker.ammusic.models.Song
import com.codetaker.ammusic.repositories.SongsRepository

class HomeViewModel(
    private val repository: SongsRepository,
) : ViewModel() {

    val songsList: LiveData<List<Song>> = repository.songsList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }

    fun setCurrentPosition(position: Int) {
        repository.setCurrentPosition(position)
    }
}