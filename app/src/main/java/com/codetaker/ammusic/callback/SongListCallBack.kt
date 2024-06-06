package com.codetaker.ammusic.callback

import com.codetaker.ammusic.models.Song

interface SongListCallBack {
    fun onClick(song: Song)
}