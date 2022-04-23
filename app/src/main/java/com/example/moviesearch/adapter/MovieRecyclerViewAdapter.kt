package com.example.moviesearch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviesearch.R
import com.example.moviesearch.restapi.MovieDTO

class MovieRecyclerViewAdapter(private val context: Context) : RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder>() {
    private val movieItems by lazy { ArrayList<MovieDTO>() }
    private lateinit var onItemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.movie_title)
        val pubDateTextView: TextView = view.findViewById(R.id.movie_pub_date)
        val userRatingTextView: TextView = view.findViewById(R.id.movie_rating)
        val movieImageView: ImageView = view.findViewById(R.id.movie_image_view)

        init {
            view.setOnClickListener {
                val position = bindingAdapterPosition
                if(position != RecyclerView.NO_POSITION) onItemClickListener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movieItem = movieItems[position]
        holder.apply {
            titleTextView.text = String.format("제목 : %s", movieItem.title)
            pubDateTextView.text = String.format("출시: %s", movieItem.pubDate)
            userRatingTextView.text = String.format("평점: %s", movieItem.userRating)

            // Glide
            Glide.with(context)
                .load(movieItem.imageLink)
                .error(R.drawable.image_not_found)
                .into(movieImageView)
        }
    }

    override fun getItemCount(): Int = movieItems.size

    fun getItem(position: Int): MovieDTO = movieItems[position]

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    fun addItem(movieList: List<MovieDTO>) {
        movieList.forEach { movieItems.add(it) }
        notifyItemInserted(movieItems.size-1)
    }

    fun clearItem() {
        movieItems.clear()
        notifyDataSetChanged()
    }
}