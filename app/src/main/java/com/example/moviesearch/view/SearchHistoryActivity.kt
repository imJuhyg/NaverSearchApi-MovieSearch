package com.example.moviesearch.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
    private val searchHistoryRecyclerViewAdapter by lazy { SearchHistoryRecyclerViewAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        binding.movieTitleRecyclerView.apply {
            layoutManager = GridLayoutManager(this@SearchHistoryActivity, 3)
            adapter = searchHistoryRecyclerViewAdapter
            addItemDecoration(CustomItemDecoration(10))
        }

        // 검색 이력 Observer
        localDatabaseViewModel.searchHistory.observe(this, {
            if(it.isEmpty()) binding.noSearchHistoryTextView.visibility = View.VISIBLE
            else searchHistoryRecyclerViewAdapter.addItem(it) // Add item
        })
        localDatabaseViewModel.getSearchHistory(10) // 검색 이력 가져오기

        // 리사이클러뷰 아이템 클릭 이벤트
        searchHistoryRecyclerViewAdapter.setOnItemClickListener(object: SearchHistoryRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // 검색명을 MainActivity 로 반환
                val searchWord = searchHistoryRecyclerViewAdapter.getItem(position)
                val intent = intent.putExtra("SEARCH_WORD", searchWord)
                setResult(RESULT_OK, intent)
                finish()
            }
        })

        // Room DB - 검색 이력 호출 에러 발생 observer
        localDatabaseViewModel.isSelectFailed.observe(this, {
            Toast.makeText(this, "검색 이력 불러오기를 실패했습니다.", Toast.LENGTH_SHORT).show()
        })
    }
}