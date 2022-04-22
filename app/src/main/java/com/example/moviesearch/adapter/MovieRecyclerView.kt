package com.example.moviesearch.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviesearch.R
import com.example.moviesearch.searchapi.MovieDTO

class MovieRecyclerView(private val context: Context) : RecyclerView.Adapter<MovieRecyclerView.ViewHolder>() {
    private val movieItems by lazy { ArrayList<MovieDTO>() }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.movie_title)
        val pubDateTextView: TextView = view.findViewById(R.id.movie_pub_date)
        val userRatingTextView: TextView = view.findViewById(R.id.movie_rating)
        val movieImageView: ImageView = view.findViewById(R.id.movie_image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movieItem = movieItems[position]
        holder.apply {
            titleTextView.text = movieItem.title
            pubDateTextView.text = movieItem.pubDate
            userRatingTextView.text = movieItem.userRating

            // Glide
            Glide.with(context)
                .load(movieItem.imageLink)
                .into(movieImageView)
            // TODO image url error handling
        }
    }

    override fun getItemCount(): Int = movieItems.size

    fun addItem(movieList: List<MovieDTO>) {
        for(movie in movieList) {
            movieItems.add(movie)
        }
        notifyItemInserted(movieItems.size-1)
    }

    fun clearItem() {
        movieItems.clear()
        notifyDataSetChanged()
    }
}