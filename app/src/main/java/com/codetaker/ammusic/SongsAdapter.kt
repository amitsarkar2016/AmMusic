package com.codetaker.ammusic

import androidx.recyclerview.widget.RecyclerView
import com.codetaker.ammusic.SongsAdapter.OnRecyclerViewClickListener
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.codetaker.ammusic.R
import android.widget.TextView
import java.util.ArrayList

class SongsAdapter(private val userList: ArrayList<String>) :
    RecyclerView.Adapter<SongsAdapter.ViewHolder>() {
    private var listener: OnRecyclerViewClickListener? = null

    interface OnRecyclerViewClickListener {
        fun OnItemClick(position: Int)
    }

    fun OnRecyclerViewClickListener(listener: OnRecyclerViewClickListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false), listener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.songName.text = userList[position]
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder(itemView: View, listener: OnRecyclerViewClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        val songName: TextView

        init {
            songName = itemView.findViewById(R.id.song_name)
            itemView.setOnClickListener { v: View? ->
                if (listener != null && adapterPosition != RecyclerView.NO_POSITION) {
                    listener.OnItemClick(adapterPosition)
                }
            }
        }
    }
}