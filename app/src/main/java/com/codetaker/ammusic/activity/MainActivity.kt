package com.codetaker.ammusic.activity

import android.Manifest
import com.codetaker.ammusic.activity.SongsAdapter.OnRecyclerViewClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.Bundle
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.single.PermissionListener
import com.karumi.dexter.listener.PermissionGrantedResponse
import android.content.Intent
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.PermissionToken
import android.annotation.SuppressLint
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.widget.ListView
import android.widget.SearchView
import com.codetaker.ammusic.R
import com.codetaker.ammusic.db.Song
import com.codetaker.ammusic.db.SongDao
import com.codetaker.ammusic.db.SongDaoImpl
import com.codetaker.ammusic.db.SongDatabaseHelper
import com.karumi.dexter.listener.PermissionRequest
import java.io.File

class MainActivity : AppCompatActivity() {
    var listView: ListView? = null
    var mrecyclerView: RecyclerView? = null
    var layoutManager = LinearLayoutManager(this)
    var songList = ArrayList<String>()
    var adapter: SongsAdapter? = null
    var searchView: SearchView? = null
    var mySongs: ArrayList<File>? = null
    lateinit var mySongsPair: ArrayList<Pair<File,String>>
    lateinit var mySongsTriple: ArrayList<Triple<File,String,Bitmap>>
    var mySongs2: MutableList<File>? = null
    lateinit var songDao: SongDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mrecyclerView = findViewById(R.id.mrecyclerView)
        searchView = findViewById(R.id.searchView)
        mrecyclerView!!.layoutManager = layoutManager

        songDao = SongDaoImpl(this)
//        val songs = fetchSongsWithAlbum()
//        for (song in songs) {
//            val newSong = Song(id = 0, title = song.first.name, artist = song.second, album = song.second, path = song.first)
//            songDao.addSong(Song(id = 0, title = song.first.name, artist = song.second, album = song.second, path = song.first))
//        }



        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
//                    mySongs = fetchSongs(Environment.getExternalStorageDirectory())
                    mySongs = fetchSongs2()
//                    mySongsPair = fetchSongsWithAlbumOld()
//                    mySongsTriple = fetchSongsWithAlbum()
//                    for (i in mySongsTriple.indices) {
//                        songList.add(mySongs!![i].name.replace(".mp3", ""))
//                        val retriever = MediaMetadataRetriever()
//                        retriever.setDataSource(mySongsPair[i].second)

//                        val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
//
//                        val v = retriever.release()
//                        songList.add(album?:"Unknown")
//                        songList.add(mySongsTriple[i].second)
//                    }
//                    adapter = SongsAdapter(mySongs!!)
                    adapter = SongsAdapter(songDao.getAllSongs())
                    mrecyclerView!!.adapter = adapter
                    adapter!!.clickListener(object : OnRecyclerViewClickListener {
                        override fun click(position: Int) {
                            val bundle = Bundle()
                            val intent = Intent(this@MainActivity, PlaySong::class.java)
                            val currentSong =
                                mrecyclerView!!.findViewHolderForAdapterPosition(position).toString()
                            intent.putExtra("songList", mySongs)
                            intent.putExtra("currentSong", currentSong)
                            intent.putExtra("position", position)
                            startActivity(intent)
                        }
                    })
                }

                override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {}
                override fun onPermissionRationaleShouldBeShown(
                    permissionRequest: PermissionRequest,
                    permissionToken: PermissionToken
                ) {
                    permissionToken.continuePermissionRequest()
                }
            })
            .check()
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Handle the search query
                return false
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String): Boolean {
                for (songName in mySongs!!) {
                    if (songName.name.contains(newText)) {
                        songList.add(songName.name.replace(".mp3", ""))
                    }
                }
                adapter!!.notifyDataSetChanged()
                return false
            }
        })
    }
    @SuppressLint("Range")
    fun fetchSongs2(): ArrayList<File> {
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM)
        val selection = "${MediaStore.Audio.Media.DATA} like ? and ${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val selectionArgs = arrayOf("%mp3")
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
        val cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder)

        val songs = ArrayList<File>()
        if (cursor != null) {
            val columnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            if (columnIndex != -1) {
                while (cursor.moveToNext()) {
                    val songPath = cursor.getString(columnIndex)
                    val albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    songs.add(File(songPath, albumName))
                }
            }
            cursor.close()
        }

        return songs
    }

    fun fetchSongsWithAlbumOld(): ArrayList<Pair<File, String>> {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST
        )
        val selection = "${MediaStore.Audio.Media.DATA} like ? and ${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val selectionArgs = arrayOf("%mp3")
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        val songs = ArrayList<Pair<File, String>>()
        if (cursor != null) {
            val dataIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val albumIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            if (dataIndex != -1 && albumIndex != -1) {
                while (cursor.moveToNext()) {
                    val songPath = cursor.getString(dataIndex)
                    val albumName = cursor.getString(albumIndex)
                    songs.add(Pair(File(songPath), albumName))
                }
            }
            cursor.close()
        }
        return songs
    }
    fun fetchSongsWithAlbum() {
        // Fetch all songs
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM_ID)
        val selection = "${MediaStore.Audio.Media.DATA} like ? and ${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val selectionArgs = arrayOf("%mp3")
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        if (cursor != null) {
            val dataIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val albumIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val songPath = cursor.getString(dataIndex)
                val albumName = cursor.getString(albumIndex)
                val albumId = cursor.getLong(albumIdIndex)
//                val albumArt = getAlbumArt(albumId)
                songDao.addSong(Song(id = 0, title = File(songPath).name, artist = albumName, album = albumName, path = File(songPath)))
            }
            cursor.close()
        }
    }

    private fun getAlbumArt(albumId: Long): Bitmap? {
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)
        val projection = arrayOf(MediaStore.Audio.Albums.ALBUM_ART)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        var albumArt: Bitmap? = null

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val albumArtString = cursor.getString(0)
                if (albumArtString != null) {
                    albumArt = BitmapFactory.decodeFile(albumArtString)
                }
            }
            cursor.close()
        }

        return albumArt
    }


}