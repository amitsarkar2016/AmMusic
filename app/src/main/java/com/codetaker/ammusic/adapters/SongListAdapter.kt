package com.codetaker.ammusic.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codetaker.ammusic.models.Song
import com.codetaker.ammusic.callback.SongListCallBack
import com.codetaker.ammusic.databinding.SongItemBinding

class SongListAdapter(
    private val songListCallBack: SongListCallBack,
) : ListAdapter<Song, SongListAdapter.ViewHolder>(diffUtil) {

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem.filePath == newItem.filePath
            }

            override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SongItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: SongItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Song) {
            binding.songName.text = item.title
            binding.albumName.text = item.artist
            binding.root.setOnClickListener {
                songListCallBack.onClick(item)
            }
        }
    }
}