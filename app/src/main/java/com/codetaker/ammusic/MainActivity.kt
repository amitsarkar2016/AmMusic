package com.codetaker.ammusic

import android.Manifest
import com.codetaker.ammusic.SongsAdapter.OnRecyclerViewClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.Bundle
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.single.PermissionListener
import com.karumi.dexter.listener.PermissionGrantedResponse
import android.os.Environment
import android.content.Intent
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.PermissionToken
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.ListView
import android.widget.SearchView
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
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
    var mySongs2: MutableList<File>? = null

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaController: MediaControllerCompat
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val prevPendingIntent: PendingIntent by lazy { createPendingIntent(ACTION_PREV) }
    private val pausePendingIntent: PendingIntent by lazy { createPendingIntent(ACTION_PAUSE) }
    private val nextPendingIntent: PendingIntent by lazy { createPendingIntent(ACTION_NEXT) }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_ID = "music_channel"
        const val EXTRA_ACTION = "com.codetaker.ammusic.extra.ACTION"
        const val ACTION_PREV = 0
        const val ACTION_PAUSE = 1
        const val ACTION_NEXT = 2
    }

    private fun createPendingIntent(action: Int): PendingIntent {
        val intent = Intent(this, PlaySong::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_ACTION, action)
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Notification().createNotification(this)
        mrecyclerView = findViewById(R.id.mrecyclerView)
        searchView = findViewById(R.id.searchView)
        mrecyclerView!!.layoutManager = layoutManager

        val service = NotificationService(applicationContext)
        service.showNotification(Counter.value)

        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
//                    mySongs = fetchSongs(Environment.getExternalStorageDirectory())
                    mySongs = fetchSongs2()
                    for (i in mySongs!!.indices) {
                        songList.add(mySongs!![i].name.replace(".mp3", ""))
                    }
//                    adapter = SongsAdapter(mySongs!!)
                    adapter = SongsAdapter(songList)
                    mrecyclerView!!.adapter = adapter
                    adapter!!.OnRecyclerViewClickListener(object : OnRecyclerViewClickListener {
                        override fun OnItemClick(position: Int) {
                            val bundle = Bundle()
                            val intent = Intent(this@MainActivity, PlaySong::class.java)
                            val currentSong =
                                mrecyclerView!!.findViewHolderForAdapterPosition(position).toString()
                            intent.putExtra("songList", mySongs)
                            intent.putExtra("currentSong", currentSong)
                            intent.putExtra("position", position)
                            startActivity(intent)
                            createNotification(mySongs!!,currentSong,position)
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

//    fun fetchSongs3(): ArrayList<File> {
//        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM_ID)
//        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
//        val cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null)
//
//        val songs = mutableListOf<Song>()
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                val songId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
//                val songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
//                val songArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
//                val albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
//                val albumArt = getAlbumArt(albumId)
//                songs.add(Song(songId, songName, songArtist, albumArt))
//            }
//            cursor.close()
//        }
//
//        return songs
//    }
//    fun getAlbumArt(albumId: Long): Bitmap? {
//        val contentResolver = contentResolver
//        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
//        val projection = arrayOf(MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART)
//        val selection = "${MediaStore.Audio.Albums._ID} = ?"
//        val selectionArgs = arrayOf(albumId.toString())
//
//        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
//
//        var albumArt: Bitmap? = null
//        if (cursor != null && cursor.moveToFirst()) {
//            val albumArtPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
//            if (albumArtPath != null) {
//                albumArt = BitmapFactory.decodeFile(albumArtPath)
//            }
//            cursor.close()
//        }
//        return albumArt
//    }

    fun fetchSongs2(): ArrayList<File> {
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA)
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
                    songs.add(File(songPath))
                }
            }
            cursor.close()
        }

        return songs
    }


    @Suppress("UNCHECKED_CAST")
    fun fetchSongs(file: File): ArrayList<File> {
        val arrayList: ArrayList<File> = ArrayList<File>()
        val songs = file.listFiles()
        if (songs != null) {
            for (myFile in songs) {
                if (!myFile.isHidden && myFile.isDirectory) {
                    arrayList.addAll(fetchSongs(myFile!!))
                } else {
                    if (myFile.name.endsWith(".mp3") && !myFile.name.startsWith(".")) {
                        arrayList.add(myFile)
                    }
                }
            }
        }
        return arrayList as ArrayList<File>
    }

    private fun createNotification(mySongs: ArrayList<File>, currentSong: String, position: Int) {
        mediaSession = MediaSessionCompat(this, "MusicService")
        mediaController = MediaControllerCompat(this, mediaSession.sessionToken)
        mediaSession.isActive = true
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Music",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Create a media style notification
        val albumArt = BitmapFactory.decodeResource(resources, R.drawable.logo)
        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(mySongs[position].name)
            .setContentText(mySongs[position].name)
            .setLargeIcon(albumArt)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .addAction(R.drawable.previous, "Previous", prevPendingIntent)
            .addAction(R.drawable.pause, "Pause", pausePendingIntent)
            .addAction(R.drawable.next, "Next", nextPendingIntent)

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}