package com.codetaker.ammusic.activity

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codetaker.ammusic.databinding.SongItemBinding
import com.codetaker.ammusic.db.Song
import java.io.File

class SongsAdapter(private val mList: List<Song>) :
    RecyclerView.Adapter<SongsAdapter.ViewHolder>() {
    private var listener: OnRecyclerViewClickListener? = null

    interface OnRecyclerViewClickListener {
        fun click(position: Int)
    }

    fun clickListener(listener: OnRecyclerViewClickListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val from = LayoutInflater.from(parent.context)
        val inflate = SongItemBinding.inflate(from, parent, false)
        return ViewHolder(inflate, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            binding.songName.text = mList[position].title.replace(".mp3", "")
            binding.albumName.text = mList[position].album
//            binding.companyImage.setImageBitmap(mList[position].third)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(val binding: SongItemBinding, listener: OnRecyclerViewClickListener?) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                if (listener != null && adapterPosition != RecyclerView.NO_POSITION) {
                    listener.click(adapterPosition)
                }
            }
        }
    }
}