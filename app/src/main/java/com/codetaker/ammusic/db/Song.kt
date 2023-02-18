package com.codetaker.ammusic.db

import java.io.File

data class Song(
    val id: Long = 0,
    val title: String,
    val artist: String,
    val album: String,
    val path: File
)