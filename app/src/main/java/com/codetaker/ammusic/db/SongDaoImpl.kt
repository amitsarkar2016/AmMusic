package com.codetaker.ammusic.db

import android.content.Context

class SongDaoImpl(context: Context) : SongDao {

    private val dbHelper: SongDatabaseHelper = SongDatabaseHelper(context)

    override fun addSong(song: Song): Long {
        return dbHelper.addSong(song)
    }

    override fun getAllSongs(): List<Song> {
        return dbHelper.getAllSongs()
    }

    override fun getSongById(id: Int): Song? {
        return dbHelper.getSong(id.toLong())
    }

    override fun updateSong(song: Song): Int {
        return dbHelper.updateSong(song)
    }

    override fun deleteSong(id: Int): Boolean {
        return dbHelper.deleteSong(id)
    }

}
