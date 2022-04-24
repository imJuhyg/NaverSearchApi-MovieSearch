package com.example.moviesearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moviesearch.adapter.SearchHistoryRecyclerViewAdapter
import com.example.moviesearch.databinding.ActivitySearchHistoryBinding
import com.example.moviesearch.util.CustomItemDecoration
import com.example.moviesearch.viewmodel.LocalDatabaseViewModel

class SearchHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchHistoryBinding
    private val localDatabaseViewModel by lazy {
        ViewModelProvider(this).get(LocalDatabaseViewModel::class.java)
    }
    private val movieTitleRecyclerViewAdapter by lazy { SearchHistoryRecyclerViewAdapter() }

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
        localDatabaseViewModel.searchHistoryLiveData.observe(this, {
            if(it.isEmpty()) binding.noSearchHistoryTextView.visibility = View.VISIBLE
            else movieTitleRecyclerViewAdapter.addItem(it) // Add item
        })
        localDatabaseViewModel.getSearchHistory(10) // 검색 이력 가져오기

        // 리사이클러뷰 아이템 클릭 이벤트
        movieTitleRecyclerViewAdapter.setOnItemClickListener(object: SearchHistoryRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val movieTitle = movieTitleRecyclerViewAdapter.getItem(position)
                val intent = intent.putExtra("SEARCH_WORD", movieTitle)
                setResult(RESULT_OK, intent)
                finish()
            }
        })
    }
}