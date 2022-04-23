package com.example.moviesearch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesearch.R

class MovieTitleRecyclerViewAdapter : RecyclerView.Adapter<MovieTitleRecyclerViewAdapter.ViewHolder>() {
    private val movieTitleItems by lazy { ArrayList<String>() }
    private lateinit var onItemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val movieTitleTextView: TextView = view.findViewById(R.id.movie_title)

        init {
            view.setOnClickListener {
                val position = bindingAdapterPosition
                if(position != RecyclerView.NO_POSITION) onItemClickListener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_movie_title, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movieTitle = movieTitleItems[position]
        holder.movieTitleTextView.text = movieTitle
    }

    override fun getItemCount(): Int = movieTitleItems.size

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    fun addItem(movieTitleList: List<String>) {
        movieTitleList.forEach { movieTitleItems.add(it) }
        notifyItemInserted(movieTitleList.size-1)
    }

    fun getItem(position: Int): String = movieTitleItems[position]
}