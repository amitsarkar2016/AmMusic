package com.codetaker.ammusic.activity

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.codetaker.ammusic.R
import java.io.File

class NewMainActivity : AppCompatActivity() {

    private lateinit var musicListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_main)

        musicListView = findViewById(R.id.musicListView)

        if (!hasPermissions()) {
            requestPermissions()
        } else {
            showMusicList()
        }
    }

    private fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_MEDIA_AUDIO), PERMISSION_REQUEST_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showMusicList()
            } else {
                Toast.makeText(this, "Permissions denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showMusicList() {
        val musicList = mutableListOf<String>()

        // Fetching music files from MediaStore
        val projection = arrayOf(
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null
        )

        cursor?.use {
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            while (it.moveToNext()) {
                val name = it.getString(nameColumn)
                val data = it.getString(dataColumn)
                Log.d("MusicList", "Found: $name at $data")
                musicList.add("$name\n$data")
            }
        }

        // Fetching music files from external storage directory
        val externalMusicList = fetchSongs(Environment.getExternalStorageDirectory())
        for (file in externalMusicList) {
            musicList.add("${file.name}\n${file.path}")
        }

        if (musicList.isEmpty()) {
            Toast.makeText(this, "No music files found.", Toast.LENGTH_SHORT).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, musicList)
        musicListView.adapter = adapter
    }

    private fun fetchSongs(file: File): List<File> {
        val arrayList = mutableListOf<File>()
        val songs = file.listFiles()
        if (songs != null) {
            for (myFile in songs) {
                if (!myFile.isHidden && myFile.isDirectory) {
                    arrayList.addAll(fetchSongs(myFile))
                } else {
                    if (myFile.name.endsWith(".mp3") && !myFile.name.startsWith(".")) {
                        arrayList.add(myFile)
                    }
                }
            }
        }
        return arrayList
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }
}
