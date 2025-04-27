package com.codetaker.ammusic.ui.home

import com.codetaker.ammusic.models.Song

interface SongListCallBack {
    fun onClick(song: Song)
}