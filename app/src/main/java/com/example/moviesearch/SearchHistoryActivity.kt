package com.example.moviesearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.moviesearch.adapter.MovieTitleRecyclerViewAdapter
import com.example.moviesearch.databinding.ActivitySearchHistoryBinding
import com.example.moviesearch.util.CustomItemDecoration
import com.example.moviesearch.viewmodel.LocalDatabaseViewModel

class SearchHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchHistoryBinding
    private val localDatabaseViewModel by lazy {
        ViewModelProvider(this).get(LocalDatabaseViewModel::class.java)
    }
    private val movieTitleRecyclerViewAdapter by lazy { MovieTitleRecyclerViewAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        binding.movieTitleRecyclerView.apply {
            layoutManager = GridLayoutManager(this@SearchHistoryActivity, 3)
            adapter = movieTitleRecyclerViewAdapter
            addItemDecoration(CustomItemDecoration(10))
        }

        // 검색 이력 Observer
        localDatabaseViewModel.searchNameLiveData.observe(this, {
            it.forEach { Log.d("debug", it) }
            if(it.isEmpty()) binding.noSearchHistoryTextView.visibility = View.VISIBLE
            else movieTitleRecyclerViewAdapter.addItem(it) // Add item
        })
        localDatabaseViewModel.getSearchHistory(10) // 검색 이력 가져오기

        movieTitleRecyclerViewAdapter.setOnItemClickListener(object: MovieTitleRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val movieTitle = movieTitleRecyclerViewAdapter.getItem(position)
                val intent = intent.putExtra("SEARCH_NAME", movieTitle)
                setResult(RESULT_OK, intent)
                finish()
            }
        })
    }
}