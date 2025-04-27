package com.codetaker.ammusic.ui.playsong

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codetaker.ammusic.models.Song
import com.codetaker.ammusic.data.repositories.SongsRepository

class PlayMusicViewModel(
    private val repository: SongsRepository,
) : ViewModel() {

    val songsList: LiveData<List<Song>> = repository.songsList

    fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }

    fun setCurrentPosition(position: Int) {
        repository.setCurrentPosition(position)
    }
}