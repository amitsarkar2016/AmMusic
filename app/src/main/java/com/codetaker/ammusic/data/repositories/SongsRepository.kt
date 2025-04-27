package com.codetaker.ammusic.data.repositories

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import com.codetaker.ammusic.data.local.db.AppDatabase
import com.codetaker.ammusic.models.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SongsRepository(
    private val context: Application,
    private val appDatabase: AppDatabase,
) {

    init {
        loadSongs()
    }

    private var currentPosition = MutableLiveData(0)
    fun getCurrentPosition(): Int {
        return currentPosition.value ?: 0
    }

    fun setCurrentPosition(position: Int) {
        currentPosition.value = position
    }

    val songsList = MutableLiveData<List<Song>>()
    fun loadSongs() {
        CoroutineScope(Dispatchers.IO).launch {
            val songs = fetchSongsFromMediaStore(context)
            appDatabase.songDao().insertAll(songs)
            songsList.postValue(appDatabase.songDao().getAllSongs())
        }
    }

    @SuppressLint("Range")
    private fun fetchSongsFromMediaStore(context: Context): List<Song> {
        val contentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(uri, null, null, null, null)

        val songs = mutableListOf<Song>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID))
                val albumArtUri = "content://media/external/audio/albumart/$albumId"
                val dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED))
                if (data.endsWith(".mp3")) {
                    val song = Song(title, artist, album, duration, dateAdded, albumId, albumArtUri, data)
                    songs.add(song)
                }
            } while (cursor.moveToNext())
        }

        cursor?.close()
        return songs
    }

    fun getSong(position: Int): Song? {
        return songsList.value?.get(position)
    }

    fun getSongs() {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.songDao().deleteAll()
            loadSongs()
        }
    }

    suspend fun searchSongs(query: String): List<Song> {
        return appDatabase.songDao().searchSongs("%$query%")
    }
}
