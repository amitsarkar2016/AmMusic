package com.codetaker.ammusic.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File

@SuppressLint("Range")
class SongDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "song_database"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "songs"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_ARTIST = "artist"
        private const val COLUMN_ALBUM = "album"
        private const val COLUMN_PATH = "path"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_ARTIST TEXT, " +
                "$COLUMN_ALBUM TEXT, " +
                "$COLUMN_PATH TEXT" +
                ")"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addSong(song: Song): Long {
        val db = writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_TITLE = ? AND $COLUMN_ARTIST = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(song.title, song.artist))
        return if (cursor.count > 0) {
            // Song already exists in database, don't add it again
            cursor.moveToFirst()
            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
            cursor.close()
            id.toLong()
        } else {
            // Song doesn't exist in database, add it
            val values = ContentValues().apply {
                put(COLUMN_TITLE, song.title)
                put(COLUMN_ARTIST, song.artist)
                put(COLUMN_ALBUM, song.album)
                put(COLUMN_PATH, song.path.absolutePath)
            }
            val id = db.insert(TABLE_NAME, null, values)
            db.close()
            id
        }
    }


    fun getAllSongs(): ArrayList<Song> {
        val songs = ArrayList<Song>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = readableDatabase
        val cursor: Cursor? = db.rawQuery(selectQuery, null)
        if (cursor?.moveToFirst() == true) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                val artist = cursor.getString(cursor.getColumnIndex(COLUMN_ARTIST))
                val album = cursor.getString(cursor.getColumnIndex(COLUMN_ALBUM))
                val path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH))
                songs.add(Song(id, title, artist, album, File(path)))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        db.close()
        return songs
    }


    fun getSong(id: Long): Song? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_TITLE, COLUMN_ARTIST, COLUMN_ALBUM, COLUMN_PATH),
            "$COLUMN_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )
        val song: Song? = if (cursor?.moveToFirst() == true) {
            val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
            val artist = cursor.getString(cursor.getColumnIndex(COLUMN_ARTIST))
            val album = cursor.getString(cursor.getColumnIndex(COLUMN_ALBUM))
            val path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH))
            Song(id, title, artist, album, File(path))
        } else {
            null
        }
        cursor?.close()
        db.close()
        return song
    }

    fun updateSong(song: Song): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, song.title)
            put(COLUMN_ARTIST, song.artist)
            put(COLUMN_ALBUM, song.album)
            put(COLUMN_PATH, song.path.absolutePath)
        }
        val rowsUpdated = db.update(
            TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(song.id.toString())
        )
        db.close()
        return rowsUpdated
    }

    fun deleteSong(songId: Int): Boolean {
        val db = writableDatabase
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(songId.toString())
        val deletedRows = db.delete(TABLE_NAME, selection, selectionArgs)
        db.close()
        return deletedRows > 0
    }
}