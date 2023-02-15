package com.codetaker.ammusic

data class Song(val songTitle: String, val artistName: String, val albumArt: ByteArray?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Song

        if (songTitle != other.songTitle) return false
        if (artistName != other.artistName) return false
        if (albumArt != null) {
            if (other.albumArt == null) return false
            if (!albumArt.contentEquals(other.albumArt)) return false
        } else if (other.albumArt != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = songTitle.hashCode()
        result = 31 * result + artistName.hashCode()
        result = 31 * result + (albumArt?.contentHashCode() ?: 0)
        return result
    }
}
