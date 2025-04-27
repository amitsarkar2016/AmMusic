package com.codetaker.ammusic.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val dateAdded: Long,
    val albumId: Long,
    val albumArtPath: String?,
    @PrimaryKey val filePath: String,
    var isFavorite: Boolean = false,
    var isPlaying: Boolean = false,
)