package com.example.moviesearch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesearch.R

class SearchHistoryRecyclerViewAdapter : RecyclerView.Adapter<SearchHistoryRecyclerViewAdapter.ViewHolder>() {
    private val searchWordItems by lazy { ArrayList<String>() }
    private lateinit var onItemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val searchWordTextView: TextView = view.findViewById(R.id.search_word_text_view)

        init {
            view.setOnClickListener {
                val position = bindingAdapterPosition
                if(position != RecyclerView.NO_POSITION) onItemClickListener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_search_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.searchWordTextView.text = searchWordItems[position]
    }

    override fun getItemCount(): Int = searchWordItems.size

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    fun addItem(movieTitleList: List<String>) {
        movieTitleList.forEach { searchWordItems.add(it) }
        notifyItemInserted(movieTitleList.size-1)
    }

    fun getItem(position: Int): String = searchWordItems[position]
}