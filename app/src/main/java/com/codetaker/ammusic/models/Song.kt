package com.codetaker.ammusic.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    @PrimaryKey val filePath: String,
)