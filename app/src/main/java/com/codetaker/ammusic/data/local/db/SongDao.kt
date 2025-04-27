package com.codetaker.ammusic.data.local.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codetaker.ammusic.models.Song

@Dao
interface SongDao {
    @Query("SELECT * FROM songs")
    fun getAllSongs(): List<Song>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(songs: List<Song>)

    @Query("SELECT * FROM songs WHERE title LIKE :query OR artist LIKE :query")
    suspend fun searchSongs(query: String): List<Song>

    @Query("DELETE FROM songs")
    suspend fun deleteAll()
}
