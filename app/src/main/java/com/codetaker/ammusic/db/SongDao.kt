package com.codetaker.ammusic.db

interface SongDao {

    fun addSong(song: Song): Long

    fun getAllSongs(): List<Song>

    fun getSongById(id: Int): Song?

    fun updateSong(song: Song): Int

    fun deleteSong(id: Int): Boolean
}
