package com.codetaker.ammusic.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.codetaker.ammusic.MediaPlayerHelper
import com.codetaker.ammusic.databinding.ActivityPlaySongBinding

class MainActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityPlaySongBinding

    private lateinit var mediaPlayerHelper: MediaPlayerHelper
    private var songsList = ArrayList<String>()
    private var currentPosition = 0


    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the MediaPlayerHelper instance
        mediaPlayerHelper = MediaPlayerHelper(this)

        // Get all the mp3 files in the device
        val contentResolver = contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(uri, null, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                if (data.endsWith(".mp3")) {
                    songsList.add(data)
                }
            } while (cursor.moveToNext())
        }

        cursor?.close()

        // Play the first song
        playSong(currentPosition)

        // Set up a button to play the next song
        binding.next.setOnClickListener {
            currentPosition++
            if (currentPosition >= songsList.size) {
                currentPosition = 0
            }
            playSong(currentPosition)
        }
    }
    private fun playSong(position: Int) {
        val songPath = songsList[position]
        mediaPlayerHelper.playAudio(songPath, completionListener = {
            currentPosition++
            if (currentPosition >= songsList.size) {
                currentPosition = 0
            }
            playSong(currentPosition)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerHelper.releaseMediaPlayer()
    }
}
